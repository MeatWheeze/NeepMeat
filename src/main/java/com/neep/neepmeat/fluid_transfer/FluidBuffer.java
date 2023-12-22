package com.neep.neepmeat.fluid_transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.util.math.Direction;

// This may be slightly unnecessary
public interface FluidBuffer extends SingleSlotStorage<FluidVariant>
{
    long getAmount();
    FluidVariant getResource();

    interface FluidBufferProvider
    {
        Storage<FluidVariant> getBuffer(Direction direction);
        void setNeedsUpdate(boolean needsUpdate);
    }
}
