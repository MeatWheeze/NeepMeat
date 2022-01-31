package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface FluidAcceptor
{
    static boolean isConnectedIn(BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof PipeBlock)
        {
            return state.get(PipeBlock.DIR_TO_CONNECTION.get(direction));
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

    enum AcceptorModes
    {

        NONE(0),
        PULL(-1),
        PUSH(1),
        INSERT_ONLY(0),
        EXTRACT_ONLY(0),
        INSERT_EXTRACT(0);

        private final float flow;

        public float getFlow()
        {
            return flow;
        }

        AcceptorModes(float pressure)
        {
            this.flow = pressure;
        }
    }

}
