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
    }

    @Override
    public String toString()
    {
        return Float.toString(pressure);
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

    public float getPressure()
    {
        return pressure;
    }

    public void addPressure(float pressure)
    {
        this.pressure += pressure;
    }

    public int getDistance()
    {
        return distance;
    }

    public void setDistance(int distance)
    {
        this.distance = distance;
    }

    @FunctionalInterface
    public interface ISpecialPipe
    {
        long apply(boolean bias, long flow);
    }
}
