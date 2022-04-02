package com.neep.neepmeat.fluid_transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

public interface FluidBuffer
{
    long getAmount();

    interface FluidBufferProvider
    {
        Storage<FluidVariant> getBuffer(Direction direction);
        void setNeedsUpdate(boolean needsUpdate);
    }
}
