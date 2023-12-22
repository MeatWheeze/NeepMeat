package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface FluidAcceptor
{
    default boolean connectInDirection(BlockState state, Direction direction)
    {
        return true;
    }

}
