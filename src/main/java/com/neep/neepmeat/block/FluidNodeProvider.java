package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface FluidNodeProvider extends DirectionalFluidAcceptor
{
    boolean connectInDirection(BlockState state, Direction direction);
}
