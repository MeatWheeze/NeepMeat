package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.machine.FluidFuelRegistry;
import com.neep.neepmeat.transport.fluid_network.FluidNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public abstract class BloodMachineBlockEntity extends SyncableBlockEntity implements FluidBuffer.FluidBufferProvider
{
    protected InputStorage inputStorage = new InputStorage();

    protected boolean enabled = true;
    public long maxRunningRate = FluidConstants.BUCKET;

    protected long runningRate;
    protected float fluidMultiplier;

    protected class InputStorage extends SnapshotParticipant<Long> implements SingleSlotStorage<FluidVariant>
    {
        long lastInput;
        FluidVariant lastFluid;

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
        {
            if (enabled && canInsert(resource))
            {
                long inserted = Math.min(maxAmount, maxRunningRate - lastInput);
                if (inserted > 0)
                {
                    updateSnapshots(transaction);
                    lastFluid = resource;
                    lastInput += inserted;
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
            return 0;
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

    public BloodMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long inCapacity, long outCapacity)
    {
        super(type, pos, state);
    }

    public boolean canInsert(FluidVariant variant)
    {
        return FluidFuelRegistry.getInstance().containsKey(variant.getFluid());
    }

    public void tick()
    {
        if (FluidNetwork.shouldTick(world.getTime()))
        {
            // Get effective influx per tick
            this.runningRate = this.inputStorage.lastInput / PipeNetwork.TICK_RATE;

            if (inputStorage.lastInput != 0)
                this.fluidMultiplier = FluidFuelRegistry.getInstance().get(inputStorage.lastFluid.getFluid()).multiplier();
            this.inputStorage.lastInput = 0;
            sync();
        }
    }

    public float getRunningRate()
    {
        return MathHelper.clamp((this.runningRate / (float) this.maxRunningRate) * fluidMultiplier, 0, maxRunningRate);
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
        nbt.putLong("lastInput", inputStorage.lastInput);
        nbt.putFloat("fluidMultiplier", fluidMultiplier);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.inputStorage.lastInput = nbt.getLong("lastInput");
        this.runningRate = nbt.getLong("runningRate");
        this.fluidMultiplier = nbt.getFloat("fluidMultiplier");
    }

    public void onUse(PlayerEntity player, Hand hand)
    {

    }

    public void clearBuffers()
    {
    }
}
