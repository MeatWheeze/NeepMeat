package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.PipeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PipeSegment extends Pair<BlockPos, Float>
{
    List<Direction> connections = new ArrayList<>();
    private float pressure;
    private int distance;
    private boolean visited;

    public PipeSegment(BlockPos pos)
    {
        super(pos, 0f);

    }
    public PipeSegment(BlockPos pos, BlockState state)
    {
        super(pos, 0f);
        if (state.getBlock() instanceof PipeBlock)
        {
            for (Direction direction : Direction.values())
            {
                if (state.get(PipeBlock.DIR_TO_CONNECTION.get(direction)) == PipeConnection.SIDE)
                {
                    connections.add(direction);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return Float.toString(pressure);
    }

    @Override
    // Ignore pressures when testing equality.
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o instanceof PipeSegment)
        {
            return ((PipeSegment) o).getPos().equals(getPos());
        }
        return false;
    }

    public BlockPos getPos()
    {
        return this.getLeft();
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

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }
}
