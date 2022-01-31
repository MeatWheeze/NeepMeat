package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.FluidBuffer;
import net.minecraft.util.math.Direction;

public interface FluidBufferProvider
{
    FluidBuffer getBuffer(Direction direction);
    void setNeedsUpdate(boolean needsUpdate);
}
