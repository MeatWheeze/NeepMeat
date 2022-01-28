package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface DirectionalFluidAcceptor extends FluidAcceptor
{
    boolean connectInDirection(BlockState state, Direction direction);

    @Override
    default AcceptorModes getDirectionMode(BlockState state, Direction direction)
    {
        if (!connectInDirection(state, direction))
        {
            return AcceptorModes.NONE;
        }
        return AcceptorModes.INSERT_EXTRACT;
    }
}
