package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.surgery.SurgeryRecipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
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

    int counter = 0;
    public void tick()
    {
        counter = Math.min(counter + 1, 10);

        robot.tick();
        Vec3d robotPos = robot.getPos();

        if (robot.isActive() && counter == 10)
        {
            MeatRecipeManager.getInstance().get(NMrecipeTypes.SURGERY, currentRecipe).ifPresent(this::nextIngredient);
            counter = 0;
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
        MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.SURGERY, context).ifPresent(this::startRecipe);
//        NeepMeat.LOGGER.info("Recipe: " + recipe);
    }

    private void startRecipe(SurgeryRecipe recipe)
    {
        this.recipeProgress = 0;
        this.currentRecipe = recipe.getId();
        Direction facing = getCachedState().get(TableControllerBlock.FACING);
        robot.setTarget(pos.add(0, 2.2, 0).offset(facing.getOpposite(), 2));
        nextIngredient(recipe);
    }

    private void interruptRecipe()
    {
        this.currentRecipe = null;
        this.recipeProgress = 0;
        robot.returnToBase();
    }

    private void finishRecipe()
    {
        MeatRecipeManager.getInstance().get(NMrecipeTypes.SURGERY, currentRecipe).ifPresent(recipe ->
        {
            Direction facing = getCachedState().get(TableControllerBlock.FACING);
            try (Transaction transaction = Transaction.openOuter())
            {
                recipe.ejectOutputs(context, transaction);
                ItemPipeUtil.storageToAny((ServerWorld) getWorld(), context.getStorage(), pos, facing, transaction);
                transaction.commit();
            }
        });

        this.currentRecipe = null;
        this.recipeProgress = 0;
        robot.returnToBase();
    }

    private void nextIngredient(SurgeryRecipe recipe)
    {
        while (true)
        {
            if (recipeProgress >= context.getSize())
            {
                finishRecipe();
                return;
            }
            RecipeInput<?> input = recipe.getInputs().get(recipeProgress);
            if (!input.isEmpty())
            {
                BlockPos itemPos = context.getPos(recipeProgress);
                if (robot.reachedTarget())
                {
                    try (Transaction transaction = Transaction.openOuter())
                    {
                        if (recipe.takeInput(context, recipeProgress, transaction))
                        {
                            transaction.commit();
                            syncBeamEffect((ServerWorld) world, robot.getPos(), Vec3d.ofCenter(itemPos, 0), Vec3d.ZERO, 20);
                        }
                        else
                        {
                            transaction.abort();
                            interruptRecipe();
                        }
                    }
                    ++recipeProgress;
                }
                return;
            }
            ++recipeProgress;
        }
    }

    private void syncBeamEffect(ServerWorld world, Vec3d pos, Vec3d end, Vec3d velocity, double showRadius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, showRadius))
        {
            Packet<?> packet = BeamPacket.create(world, GraphicsEffects.BEAM, pos, end, velocity, 0.5f, 5, MWNetwork.EFFECT_ID);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
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
