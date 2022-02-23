package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.PipeConnection;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface FluidAcceptor
{
    static boolean isConnectedIn(BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof PipeBlock)
        {
            return state.get(PipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof FluidAcceptor)
        {
            FluidAcceptor acceptor = (FluidAcceptor) state.getBlock();
            return acceptor.connectInDirection(state, direction);
        }
        return false;
    }

    default boolean connectInDirection(BlockState state, Direction direction)
    {
        return true;
    }

    default AcceptorModes getDirectionMode(BlockState state, Direction direction)
    {
        return AcceptorModes.INSERT_EXTRACT;
    }

}
