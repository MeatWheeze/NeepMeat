package com.neep.neepmeat.api.machine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.processing.FluidFuelRegistry;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public abstract class BloodMachineBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    protected InputStorage inputStorage = new InputStorage();

    protected boolean enabled = true;
    public long maxRunningRate = FluidConstants.BUCKET / 4;
    public long exhaustBufferSize = FluidConstants.BUCKET * 2;

    protected long runningRate;
    protected float fluidMultiplier;

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
                    long inserted = Math.min(maxAmount, maxRunningRate - lastInput);
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
            return maxRunningRate;
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
            FluidFuelRegistry.Entry entry = FluidFuelRegistry.getInstance().get(inputFluid);
            return entry != null
                    && BloodMachineBlockEntity.this.canInsert(inputVariant)
                    && (entry.exhaustType() == null || getResource().isOf(entry.exhaustType()) || this.isResourceBlank());
        }

        /**
         * @param fuelResource The fuel whose corresponding exhaust type is to be inserted
         */
        public long insertFromFuel(FluidVariant fuelResource, long maxAmount, TransactionContext transaction)
        {
            FluidFuelRegistry.Entry entry = FluidFuelRegistry.getInstance().get(fuelResource.getFluid());
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

    protected ExhaustStorage exhaustStorage = new ExhaustStorage(exhaustBufferSize, this::sync);

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public boolean canInsert(FluidVariant variant)
    {
        return FluidFuelRegistry.getInstance().containsKey(variant.getFluid());
    }

    public void tick()
    {
        if (FluidNodeManager.shouldTick(world.getTime()))
        {
            // Get effective influx per tick
            float prevRate = this.runningRate;
            this.runningRate = this.inputStorage.lastInput / PipeNetwork.TICK_RATE;
            if (prevRate != runningRate)
            {
                onRateChange();
            }

            // Determine power multiplier from the inserted fluid
            if (inputStorage.lastInput != 0 && inputStorage.lastFluid != null)
            {
                this.fluidMultiplier = FluidFuelRegistry.getInstance().get(inputStorage.lastFluid.getFluid()).multiplier();
            }

            // Reset input counter and fluid
            this.inputStorage.lastInput = 0;
            inputStorage.lastFluid = null;
            sync();
        }
    }

    public float getRunningRate()
    {
        return MathHelper.clamp((this.runningRate / (float) this.maxRunningRate) * fluidMultiplier, 0, 1);
    }

    protected void onRateChange() {}

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
        nbt.putLong("runningRate", runningRate);
        nbt.putLong("lastInput", inputStorage.lastInput);
        nbt.putFloat("fluidMultiplier", fluidMultiplier);
        nbt.putBoolean("enabled", enabled);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inputStorage.lastInput = nbt.getLong("lastInput");
        this.runningRate = nbt.getLong("runningRate");
        this.fluidMultiplier = nbt.getFloat("fluidMultiplier");
        this.enabled = nbt.getBoolean("enabled");
    }

    public void onUse(PlayerEntity player, Hand hand)
    {

    }

    public void clearBuffers()
    {
    }
}
