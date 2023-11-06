package com.neep.neepmeat.machine.bottler;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public class BottlerBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    private final BottlerStorage storage = new BottlerStorage(this);
    public static final float INCREMENT_MIN = 0.1f;
    public static final float INCREMENT_MAX = 1f;
    float increment;
    public static final int MAX_PROGRESS = 20;
    private float progress;
    private int maxProgress = MAX_PROGRESS;
    private long startTime;

    private State state = State.IDLE;

    public BottlerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public BottlerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.BOTTLER, pos, state);
    }

    public WritableStackStorage getItemStorage(Direction direction)
    {
        return storage.getItemStorage();
    }

    public Storage<FluidVariant> getStorage(Direction direction)
    {
        return storage.getFluidStorage();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putFloat("progress", progress);
        nbt.putFloat("increment", increment);
        nbt.putInt("maxProgress", maxProgress);
        nbt.putLong("startTime", startTime);
        nbt.putInt("state", state.ordinal());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.progress = nbt.getFloat("progress");
        this.increment = nbt.getFloat("increment");
        this.maxProgress = nbt.getInt("maxProgress");
        this.startTime = nbt.getLong("startTime");
        this.state = State.values()[nbt.getInt("state")];
    }

    private Storage<FluidVariant> getInputStorage()
    {
        return FluidStorage.SIDED.find(world, pos.down(), Direction.UP);
    }

    @Override
    public boolean tick(MotorEntity motor)
    {

        try (Transaction transaction = Transaction.openOuter())
        {
            switch (state)
            {
                case IDLE:
                {
                    {
                        tryStart(transaction);
                        transaction.commit();
                    }
                    break;
                }

                case FILLING:
                {
                    // Stop if item is removed
                    if (storage.getItemStorage().isEmpty()) interrupt();

                    // Increment progress
                    this.progress = Math.min(maxProgress, progress + increment);
                    if (progress == maxProgress)
                    {
                        progress = 0;
                        succeed(transaction);
                        transaction.commit();
                    }
                    return true;
                }

                case EJECTING:
                {
                    Direction facing = getCachedState().get(BaseHorFacingBlock.FACING);
                    if (ItemPipeUtil.storageToAny((ServerWorld) world, storage.getItemStorage(), pos, facing, transaction))
                    {
                        state = State.IDLE;
                    }
                    transaction.commit();
                    return true;
                }
            }
        }
        return false;
    }

    void interrupt()
    {
        state = State.IDLE;
        progress = 0;
        sync();
    }

    protected void tryStart(TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            if (moveToItem(inner) > 0)
            {
                state = State.FILLING;
                maxProgress = MAX_PROGRESS;
                startTime = world.getTime();
                sync();
                inner.abort();
            }
        }
    }

    protected void succeed(TransactionContext transaction)
    {
        moveToItem(transaction);
        state = State.EJECTING;
        sync();
    }

    private long moveToItem(TransactionContext transaction)
    {
        Storage<FluidVariant> inputStorage = getInputStorage();
        Storage<FluidVariant> outputStorage = storage.getFluidStorage();
        if (inputStorage != null && outputStorage != null)
        {
            return StorageUtil.move(inputStorage, outputStorage, v -> true, Long.MAX_VALUE, transaction);
        }
        return 0;
    }

    @Override
    public void setInputPower(float power)
    {
        this.increment = MathHelper.lerp(power, INCREMENT_MIN, INCREMENT_MAX);
    }

    public int getMaxProgress()
    {
        return maxProgress;
    }

    public long getStartTime()
    {
        return startTime;
    }

    private enum State
    {
        IDLE,
        FILLING,
        EJECTING
    }
}