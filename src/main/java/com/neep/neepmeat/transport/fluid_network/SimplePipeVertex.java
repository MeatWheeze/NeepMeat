package com.neep.neepmeat.transport.fluid_network;

import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplePipeVertex implements PipeVertex
{
    protected final List<Direction> connections = new ArrayList<>();
    private final PipeVertex[] adjacentVertices = new PipeVertex[6];
    protected float pressureHead;
    protected float elevationHead;

    protected long amount;
    private PipeNetwork network;

    public SimplePipeVertex()
    {
    }

    public void tick()
    {
        for (Direction direction : Direction.values())
        {
            PipeVertex adjacent = adjacentVertices[direction.ordinal()];
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
        StringBuilder adj = new StringBuilder();
        for (PipeVertex v : adjacentVertices)
        {
            if (v != null) adj.append(System.identityHashCode(v)).append(", ");
        }
        return "Vertex@"+System.identityHashCode(this)+"{connection=" + adj + ", head:" + getTotalHead() + "}";
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
            return connections.equals(vertex.connections);
        }
        return false;
    }

    public void putAdjacent(Direction direction, PipeVertex vertex)
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

    public void setElevationHead(float value)
    {
        this.elevationHead = value;
    }

    public float getTotalHead()
    {
        return pressureHead + elevationHead;
    }

    @Override
    public void setNetwork(PipeNetwork network)
    {
        this.network = network;
    }

    @Override
    public PipeNetwork getNetwork()
    {
        return network;
    }

    public List<Direction> getConnections()
    {
        return connections;
    }

    @Override
    public boolean canSimplify()
    {
        int edges = 0;
        for (PipeVertex pipeNetVertex : getAdjacentVertices())
        {
            if (pipeNetVertex != null) ++edges;
        }
        return edges == 2;
    }

    @Override
    public void reset()
    {
        pressureHead = 0;
        elevationHead = 0;
        amount = 0;
        connections.clear();
        network = null;
        Arrays.fill(adjacentVertices, null);
    }
}
