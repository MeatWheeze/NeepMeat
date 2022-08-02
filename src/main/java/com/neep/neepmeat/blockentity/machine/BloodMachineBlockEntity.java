package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.MultiTypedFluidBuffer;
import com.neep.neepmeat.api.storage.TypedFluidBuffer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public abstract class BloodMachineBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    public TypedFluidBuffer outputBuffer;
    protected MultiTypedFluidBuffer buffer;
    protected InputStorage inputStorage = new InputStorage();

    protected boolean enabled;
    public final long maxInsertRate = FluidConstants.BUCKET;

    protected long lastInput;
    protected long runningRate;

    protected class InputStorage extends SnapshotParticipant<Long> implements SingleSlotStorage<FluidVariant>
    {
        long currentInput;

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            if (enabled)
            {
                long inserted = Math.min(maxAmount, maxInsertRate - currentInput);
                if (inserted > 0)
                {
//                    snapshotParticipant.updateSnapshots(transaction);
                    updateSnapshots(transaction);
                    currentInput += inserted;
                }
                return inserted;
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public boolean isResourceBlank()
        {
            return false;
        }

        @Override
        public FluidVariant getResource()
        {
            return FluidVariant.of(Fluids.WATER);
        }

        @Override
        public long getAmount()
        {
            return 0;
        }

        @Override
        public long getCapacity()
        {
            return maxInsertRate;
        }

        @Override
        protected Long createSnapshot()
        {
            return this.currentInput;
        }

        @Override
        protected void readSnapshot(Long snapshot)
        {
            this.currentInput = snapshot;
        }
    };

    protected SnapshotParticipant<Long> snapshotParticipant = new SnapshotParticipant<>()
    {
        @Override
        protected Long createSnapshot()
        {
            return lastInput;
        }

        @Override
        protected void readSnapshot(Long snapshot)
        {
            lastInput = snapshot;
        }
    };

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long inCapacity, long outCapacity)
    {
        super(type, pos, state);
//        inputBuffer = new TypedFluidBuffer(inCapacity, fluidVariant -> fluidVariant.equals(NMFluids.CHARGED), TypedFluidBuffer.Mode.INSERT_ONLY, this::sync);
//        outputBuffer = new TypedFluidBuffer(outCapacity, fluidVariant -> fluidVariant.equals(NMFluids.UNCHARGED), TypedFluidBuffer.Mode.EXTRACT_ONLY, this::sync);
//        buffer = new MultiTypedFluidBuffer(this, List.of(inputBuffer, outputBuffer));
    }

    public long doWork(long amount, TransactionContext transaction)
    {
//        Transaction nested = transaction.openNested();
//        long extracted = inputBuffer.extractDirect(NMFluids.CHARGED, amount, nested);
////        long inserted = outputBuffer.insertDirect(NMFluids.UNCHARGED, extracted, nested);
////        if (extracted == amount && inserted == amount)
//        if (extracted == amount)
//        {
//            nested.commit();
//            return extracted;
//        }
//        nested.abort();
        return 0;
    }

    public void tick()
    {
        enabled = true;
        this.runningRate = this.inputStorage.currentInput;
        this.inputStorage.currentInput = 0;
        sync();
    }

    public long getRunningRate()
    {
        return runningRate;
    }

    @Override
    public Storage<FluidVariant> getBuffer(Direction direction)
    {
        return inputStorage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putLong("runningRate", runningRate);
        nbt.putLong("lastInput", lastInput);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.lastInput = nbt.getLong("lastInput");
        this.runningRate = nbt.getLong("runningRate");
    }

    public void onUse(PlayerEntity player, Hand hand)
    {

    }

    public void clearBuffers()
    {
    }
}
