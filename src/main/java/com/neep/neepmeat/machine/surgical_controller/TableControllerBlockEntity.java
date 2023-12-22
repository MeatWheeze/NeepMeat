package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.surgery.SurgeryRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@SuppressWarnings("UnstableApiUsage")
public class TableControllerBlockEntity extends BloodMachineBlockEntity
{
    protected int recipeProgress = 0;

    protected final SurgeryTableContext context = new SurgeryTableContext();
    protected final SurgicalRobot robot = new SurgicalRobot(getPos());

    protected Identifier currentRecipe;

    public TableControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TableControllerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TABLE_CONTROLLER, pos, state);
    }

    public void tick()
    {
        robot.tick();
        Vec3d robotPos = robot.getPos();
        ((ServerWorld) world).spawnParticles(ParticleTypes.COMPOSTER, robotPos.x, robotPos.y, robotPos.z, 5, 0, 0, 0, 0);

        if (robot.isActive() && robot.reachedTarget())
        {
            MeatRecipeManager.getInstance().get(NMrecipeTypes.SURGERY, currentRecipe).ifPresent(this::nextIngredient);
        }

        sync();
    }

    public void assemble()
    {
        context.clear();

        Direction facing = getCachedState().get(BaseHorFacingBlock.FACING).getOpposite();
        Direction left = facing.rotateYCounterclockwise();
        BlockPos corner = pos.offset(facing).offset(left).up();

        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int j = 2; j >= 0; --j)
        {
            for (int i = 0; i < 3; ++i)
            {
                Vec3i xVec = left.getOpposite().getVector().multiply(i);
                Vec3i zVec = facing.getVector().multiply(j);
                mutable.set(corner, xVec);
                mutable.set(mutable, zVec);
                context.add((ServerWorld) world, mutable);
            }
        }
    }

    public void testRecipe()
    {
        SurgeryRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.SURGERY, context).orElse(null);
        NeepMeat.LOGGER.info("Recipe: " + recipe);
        if (recipe != null)
        {
            this.recipeProgress = 0;
            this.currentRecipe = recipe.getId();
            nextIngredient(recipe);
        }
    }

    private void nextIngredient(SurgeryRecipe recipe)
    {
        while (true)
        {
            if (recipeProgress >= context.getSize())
            {
                recipeProgress = 0;
                robot.returnToBase();
                return;
            }
            RecipeInput<?> input = recipe.getInputs().get(recipeProgress);
            if (!input.isEmpty())
            {
                robot.setTarget(context.getPos(recipeProgress));
                ++recipeProgress;
                return;
            }
            ++recipeProgress;
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("recipeProgress", recipeProgress);
        nbt.putString("currentRecipe", currentRecipe != null ? currentRecipe.toString() : "null");
        robot.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.recipeProgress = nbt.getInt("recipeProgress");
        this.currentRecipe = new Identifier(nbt.getString("currentRecipe"));
        robot.readNbt(nbt);
    }
}
