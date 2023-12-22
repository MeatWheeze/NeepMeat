package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.recipe.surgery.*;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
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

    protected boolean redstone;

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
        if (!context.isAssembled)
        {
            assemble();
            context.isAssembled = true;
        }

        counter = Math.min(counter + 1, 10);

        robot.tick();

        if (robot.isActive() && counter == 10)
        {
            MeatRecipeManager.getInstance().get(TypeFilter.instanceOf(SurgeryRecipe.class), currentRecipe).ifPresent(this::nextIngredient);
            counter = 0;
        }

        sync();
    }

    public void assemble()
    {
        context.clear();

        Direction facing = getCachedState().get(BaseHorFacingBlock.FACING).getOpposite();
        Direction left = facing.rotateYCounterclockwise();
        BlockPos corner = pos.offset(facing).offset(left);

        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int j = 2; j >= 0; --j)
        {
            for (int i = 0; i < 3; ++i)
            {
                Vec3i xVec = left.getOpposite().getVector().multiply(i);
                Vec3i zVec = facing.getVector().multiply(j);
                mutable.set(corner, xVec);
                mutable.set(mutable, zVec);

                // Check the lower block first. If a structure is found, ignore the upper block.
                TableComponent<?> component = TableComponent.STRUCTURE_LOOKUP.find(world, mutable, null);
                if (component != null)
                {
                    context.add((ServerWorld) world, mutable);
                    continue;
                }

                mutable.set(mutable, Direction.UP);
                component = TableComponent.STRUCTURE_LOOKUP.find(world, mutable, null);

                // Add the upper block.
                context.add((ServerWorld) world, mutable);
            }
        }
    }

    public void tryRecipe()
    {
        GeneralSurgeryRecipe surgeryRecipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.SURGERY, context).orElse(null);
        if (surgeryRecipe != null)
        {
            startRecipe(surgeryRecipe);
            return;
        }

        TransformingToolRecipe toolRecipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.TRANSFORMING_TOOL, context).orElse(null);
        if (toolRecipe != null)
        {
            startRecipe(toolRecipe);
            return;
        }

        UpgradeInstallRecipe mobRecipe = MeatRecipeManager.getInstance().getFirstMatch(NMrecipeTypes.UPGRADE_INSTALL, context).orElse(null);
        if (mobRecipe != null)
        {
            startRecipe(mobRecipe);
            return;
        }
    }

    private void startRecipe(SurgeryRecipe recipe)
    {
        this.recipeProgress = 0;
        this.currentRecipe = recipe.getId();
        Direction facing = getCachedState().get(TableControllerBlock.FACING);
        robot.setTarget(pos.add(0, 2, 0).offset(facing.getOpposite(), 2));
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
        MeatRecipeManager.getInstance().get(TypeFilter.instanceOf(SurgeryRecipe.class), currentRecipe).ifPresent(recipe ->
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
            if (!recipe.isInputEmpty(recipeProgress))
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
            MWGraphicsEffects.syncBeamEffect(player, MWGraphicsEffects.BEAM, world, pos, end, velocity, 0.5f, 5);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("recipeProgress", recipeProgress);
        nbt.putString("currentRecipe", currentRecipe != null ? currentRecipe.toString() : "null");
        nbt.putBoolean("redstone", redstone);
        robot.writeNbt(nbt);
        context.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.recipeProgress = nbt.getInt("recipeProgress");
        this.currentRecipe = new Identifier(nbt.getString("currentRecipe"));
        this.redstone = nbt.getBoolean("redstone");
        robot.readNbt(nbt);
        context.readNbt(nbt);
    }

    public Storage<ItemVariant> getStorage(Direction direction)
    {
        Direction facing = getCachedState().get(TableControllerBlock.FACING);
        return direction == facing || direction == Direction.DOWN ? context.storage : null;
    }

    public void update(boolean receiving)
    {
        if (receiving && !redstone)
        {
            assemble();
            tryRecipe();
        }
        redstone = receiving;
    }

    public void showBlocks(PlayerEntity player)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            assemble();

            int count = 0;
            for (int i = 0; i < context.getWidth(); ++i)
            {
                for (int j = 0; j < context.getHeight(); ++j)
                {
                    int idx = i * context.getWidth() + j;
                    var thing = context.getStructure(idx);
                    var pos = context.getPos(idx);

                    if (thing != null && pos != null)
                    {
                        ++count;
                        serverWorld.spawnParticles(ParticleTypes.COMPOSTER,
                                                 pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 7,
                                                   0.15, 0.45, 0.15, 0.1);
                    }
                }
            }

            player.sendMessage(Text.of("Structure consists of " + count + " blocks."), true);
        }
    }
}
