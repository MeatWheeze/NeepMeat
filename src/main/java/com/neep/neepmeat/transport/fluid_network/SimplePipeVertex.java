package com.neep.neepmeat.transport.fluid_network;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.block.fluid_transport.ICapillaryPipe;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class SimplePipeVertex implements PipeVertex
{
    protected final List<Direction> connections = new ArrayList<>();
    private final PipeVertex[] adjacentVertices = new PipeVertex[6];
    protected float pressureHead;
    protected float elevationHead;
    protected int distance;
    protected boolean capillary;

    protected long amount;

    protected final ISpecialPipe special;
    public boolean flag;

    public SimplePipeVertex(BlockState state)
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
        else if (state.getBlock() instanceof BaseFacingBlock facing)
        {
            connections.add(state.get(BaseFacingBlock.FACING));
            connections.add(state.get(BaseFacingBlock.FACING).getOpposite());
        }
        this.capillary = state.getBlock() instanceof ICapillaryPipe;
        this.special = state.getBlock() instanceof ISpecialPipe specialPipe ? specialPipe : null;
    }

    public void tick()
    {
        for (Direction direction : Direction.values())
        {
            SimplePipeVertex adjacent = adjacentVertices[direction.ordinal()];
            if (adjacent != null)
            {
                float difference = adjacent.getTotalHead() - this.getTotalHead();
                if (difference >= 0) continue;

                long transfer = (long) Math.floor(Math.min(amount, difference * 100));
                long received = adjacent.receiveFluid(transfer);
                amount -= transfer;
            }
        }
    }

    public long receiveFluid(long amount)
    {
        this.amount += amount;
        return amount;
    }

    @Override
    public String toString()
    {
        return "PipeNetVertex{connection=" + connections + ", head:" + getTotalHead() + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) // ?????
        {
            return true;
        }
        if (o instanceof SimplePipeVertex vertex)
        {
            return getDistance() == vertex.getDistance() && connections.equals(vertex.connections);
        }
        return false;
    }

    public boolean canFluidFlow(Direction bias, BlockState vertex)
    {
        if (!isSpecial())
        {
            return true;
        }
        return special.canTransferFluid(bias, vertex);
    }

    public void putAdjacent(Direction direction, SimplePipeVertex vertex)
    {
        adjacentVertices[direction.ordinal()] = vertex;
    }

    public void putAdjacent(int dir, PipeVertex vertex)
    {
        adjacentVertices[dir] = vertex;
    }

    public PipeVertex getAdjacent(Direction direction)
    {
        return adjacentVertices[direction.ordinal()];
    }

    public PipeVertex[] getAdjacentVertices()
    {
        return adjacentVertices;
    }

    public int edges()
    {
        int number = 0;
        for (PipeVertex pipeNetVertex : adjacentVertices)
        {
            if (pipeNetVertex != null) ++number;
        }
        return number;
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

    public void setElevationHead(float value)
    {
        this.elevationHead = value;
    }

    public float getTotalHead()
    {
        return pressureHead + elevationHead;
    }

    public int getDistance()
    {
        return distance;
    }

    public List<Direction> getConnections()
    {
        return connections;
    }

}
