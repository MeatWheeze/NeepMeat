package com.neep.neepmeat.machine.small_trommel;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public class SmallTrommelBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    private static final float INCREMENT_MIN = 0.1f;
    private static final float INCREMENT_MAX = 1;
    public TrommelStorage storage;
    public FluidVariant currentFluid;

    protected int totalProgress;
    protected float progress;
    public float renderProgress;
    private float progressIncrement;

    public SmallTrommelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new TrommelStorage(this);
    }

    public SmallTrommelBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.SMALL_TROMMEL, pos, state);
    }

    public Storage<FluidVariant> getInputStorage(Direction direction)
    {
        return direction == Direction.UP ? storage.fluidInput : null;
    }

    public Storage<FluidVariant> getOutputFluidStorage()
    {
        return storage.fluidOutput;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("progress", progress);
        nbt.putFloat("progressIncrement", progressIncrement);
        nbt.putInt("totalProgress", totalProgress);
        if (currentFluid != null)
            nbt.put("currentFluid", currentFluid.toNbt());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.progress = nbt.getFloat("progress");
        this.progressIncrement = nbt.getFloat("progressIncrement");
        this.totalProgress = nbt.getInt("totalProgress");
        this.currentFluid = FluidVariant.fromNbt((NbtCompound) nbt.get("currentFluid"));
    }

    public void startDutyCycle()
    {
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        currentFluid = FluidVariant.of(Fluids.WATER);
        totalProgress = 40;
        progress = Math.min(totalProgress, progress + progressIncrement);
        sync();
        if (progress >= totalProgress)
        {
            progress = 0;
        }
        return false;
    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {
        this.progressIncrement = MathHelper.lerp(multiplier, INCREMENT_MIN, INCREMENT_MAX);
    }

    public static class Structure extends BlockEntity
    {
        public Structure(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public Structure(BlockPos pos, BlockState state)
        {
            super(NMBlockEntities.SMALL_TROMMEL_STRUCTURE, pos, state);
        }
    }
}
