package com.neep.neepmeat.fluid_transfer;

import com.neep.neepmeat.block.AbstractPipeBlock;
import com.neep.neepmeat.block.fluid_transport.ICapillaryPipe;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PipeState
{
    List<Direction> connections = new ArrayList<>();
    protected float pressure;
    protected int distance;
    protected boolean capillary;
    protected final ISpecialPipe special;

    public PipeState(BlockState state)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            for (Direction direction : Direction.values())
            {
                if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
                {
                    connections.add(direction);
                }
            }
        }
        this.capillary = state.getBlock() instanceof ICapillaryPipe;
        this.special = null;
    }

    @Override
    public String toString()
    {
//        return Float.toString(pressure);
        return "PipeState{connection=" + connections + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) // ?????
        {
            return true;
        }
        if (o instanceof PipeState state)
        {
            return getDistance() == state.getDistance() && connections.equals(state.connections);
        }
        return false;
    }

    public boolean isCapillary()
    {
        return capillary;
    }

    public boolean isSpecial()
    {
        return special != null;
    }

    public ISpecialPipe getSpecial()
    {
        return special;
    }

    public float getPressure()
    {
        return pressure;
    }

    public int getDistance()
    {
        return distance;
    }

    @FunctionalInterface
    public interface ISpecialPipe
    {
        long apply(boolean bias, long flow);
    }
}
