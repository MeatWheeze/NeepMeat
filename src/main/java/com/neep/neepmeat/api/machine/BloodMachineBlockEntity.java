package com.neep.neepmeat.api.machine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.processing.FluidEnegyRegistry;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.api.processing.PowerUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public abstract class BloodMachineBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    protected InputStorage inputStorage = new InputStorage();

    protected boolean enabled = true;
    public long exhaustBufferSize = FluidConstants.BUCKET * 2;

//    protected long runningRate;
//    protected float fluidMultiplier;

    protected double powerIn;

    protected ExhaustStorage exhaustStorage = new ExhaustStorage(exhaustBufferSize, this::markDirty);

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public boolean canInsert(FluidVariant variant)
    {
        return FluidEnegyRegistry.getInstance().containsKey(variant.getFluid());
    }

    public void tick()
    {
        if (FluidNodeManager.shouldTick(world.getTime()))
        {
            // Get effective influx per tick
            double prevPower = this.powerIn;

            // Get effective input power
            this.powerIn = PowerUtils.fluidPower(inputStorage.lastFluid, inputStorage.lastInput, PipeNetwork.TICK_RATE);

            if (prevPower != powerIn)
            {
                onPowerChange();
            }

//             Determine power baseEnergy from the inserted fluid
//            if (inputStorage.lastInput != 0 && inputStorage.lastFluid != null)
//            {
//                this.fluidMultiplier = FluidEnegyRegistry.getInstance().get(inputStorage.lastFluid.getFluid()).baseEnergy();
//            }

            // Reset input counter and fluid
            this.inputStorage.lastInput = 0;
            inputStorage.lastFluid = null;
        }
    }

    public double getPUPower()
    {
        return PowerUtils.absoluteToPerUnit(powerIn);
    }

    public long getMaxInsert()
    {
        return FluidConstants.BUCKET / 4;
    }

    protected void onPowerChange()
    {
        sync();
    }

    @Override
    public Storage<FluidVariant> getBuffer(Direction direction)
    {
//        return inputStorage;
        return direction == Direction.DOWN ? exhaustStorage : inputStorage;
    }

    public FluidPump getPump(Direction direction)
    {
        return direction == Direction.DOWN ? FluidPump.of(1, true) : null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putDouble("powerIn", powerIn);
        nbt.putLong("lastInput", inputStorage.lastInput);
        nbt.putBoolean("enabled", enabled);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inputStorage.lastInput = nbt.getLong("lastInput");
        this.powerIn = nbt.getDouble("powerIn");
        this.enabled = nbt.getBoolean("enabled");
    }

    protected class InputStorage extends SnapshotParticipant<Long> implements SingleSlotStorage<FluidVariant>
    {
        long lastInput;
        FluidVariant lastFluid;

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            // Permit only one fluid type to enter at a time and ensure that the exhaust variants match.
            if (enabled && canInsert(resource) && exhaustStorage.canAcceptExhaust(resource) && (lastFluid == null || resource.equals(lastFluid)))
            {
                try (Transaction inner = transaction.openNested())
                {
                    long inserted = Math.min(maxAmount, getMaxInsert() - lastInput);
                    if (inserted > 0)
                    {
                        updateSnapshots(inner);
                        lastFluid = resource;
                        lastInput += inserted;
                        if (exhaustStorage.insertFromFuel(resource, inserted, inner) != maxAmount)
                        {
                            inner.abort();
                            return 0;
                        }
                    }
                    inner.commit();
                    return inserted;
                }
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            return exhaustStorage.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank()
        {
            return true;
        }

        @Override
        public FluidVariant getResource()
        {
            return FluidVariant.of(Fluids.WATER);
        }

        @Override
        public long getAmount()
        {
            return exhaustStorage.getAmount();
        }

        @Override
        public long getCapacity()
        {
            return getMaxInsert();
        }

        @Override
        protected Long createSnapshot()
        {
            return this.lastInput;
        }

        @Override
        protected void readSnapshot(Long snapshot)
        {
            this.lastInput = snapshot;
        }
    };

    protected class ExhaustStorage extends WritableSingleFluidStorage
    {
        public ExhaustStorage(long capacity, Runnable finalCallback)
        {
            super(capacity, finalCallback);
        }

        /**
         * @param inputVariant The fluid to be inserted into the machine, not its exhaust.
         */
        public boolean canAcceptExhaust(FluidVariant inputVariant)
        {
            Fluid inputFluid = inputVariant.getFluid();
            FluidEnegyRegistry.Entry entry = FluidEnegyRegistry.getInstance().get(inputFluid);
            return entry != null
                    && BloodMachineBlockEntity.this.canInsert(inputVariant)
                    && (entry.exhaustType() == null || getResource().isOf(entry.exhaustType()) || this.isResourceBlank());
        }

        /**
         * @param fuelResource The fuel whose corresponding exhaust type is to be inserted
         */
        public long insertFromFuel(FluidVariant fuelResource, long maxAmount, TransactionContext transaction)
        {
            FluidEnegyRegistry.Entry entry = FluidEnegyRegistry.getInstance().get(fuelResource.getFluid());
            if (entry != null)
            {
                if (entry.hasExhaust()) return insert(entry.getExhaustVariant(), maxAmount, transaction);
                else return maxAmount;
            }
            return 0;
        }

        @Override
        public boolean supportsInsertion()
        {
            return false;
        }
    };
}
