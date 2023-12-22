package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class SimplePipeVertex implements PipeVertex
{
//    protected final List<Direction> connections = new ArrayList<>();
    private final PipeVertex[] adjacentVertices = new PipeVertex[6];
    protected float pressureHead;
    protected float elevationHead;

    protected long amount;
    protected long oldAmount;
    private PipeNetwork network;

    public SimplePipeVertex()
    {
    }

    public void tick()
    {
    }

    @Override
    public void preTick()
    {
        PipeVertex.super.preTick();
        oldAmount = amount;
    }

    public long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant variant, TransactionContext transaction)
    {
        this.amount += maxAmount;
        return maxAmount;
    }

    @Override
    public String toString()
    {
        StringBuilder adj = new StringBuilder();
        for (PipeVertex v : adjacentVertices)
        {
            if (v != null) adj.append(System.identityHashCode(v)).append(", ");
        }
        return "Vertex@"+System.identityHashCode(this)+"{connection=" + adj + "head:" + getTotalHead() + "}";
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
            return Arrays.equals(adjacentVertices, vertex.getAdjVertices());
        }
        return false;
    }

    public void putAdjacent(Direction direction, PipeVertex vertex)
    {
        adjacentVertices[direction.ordinal()] = vertex;
    }

    public PipeVertex getAdjacent(Direction direction)
    {
        return getAdjacent(direction.ordinal());
    }

    public PipeVertex getAdjacent(int dir)
    {
        return adjacentVertices[dir];
    }

    public PipeVertex[] getAdjVertices()
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

//    public List<Direction> getConnections()
//    {
//        return connections;
//    }

    protected int numEdges()
    {
        int edges = 0;
        for (PipeVertex pipeNetVertex : getAdjVertices())
        {
            if (pipeNetVertex != null) ++edges;
        }
        return edges;
    }

    @Override
    public boolean canSimplify()
    {
        return numEdges() <= 2;
    }

    @Override
    public void reset()
    {
        pressureHead = 0;
        elevationHead = 0;
        amount = 0;
        oldAmount = 0;
        network = null;
        clearEdges();
    }

    @Override
    public boolean collapseEdges()
    {
        if (canSimplify())
        {
            if (numEdges() == 2)
            {
                PipeVertex[] edge = new SimplePipeVertex[2];
                int current = 0;

                for (int i = 0; i < 6; ++i)
                {
                    if (getAdjVertices()[i] != null)
                    {
                        edge[current] = getAdjVertex(i);
                        ++current;
                    }
                }

                // Determine the directions to link together.
                for (int i = 0; i < 6; ++i)
                {
                    if (edge[0].getAdjVertex(i) == this)
                    {
                        edge[0].setAdjVertex(i, edge[1]);
                    }

                    if (edge[1].getAdjVertex(i) == this)
                    {
                        edge[1].setAdjVertex(i, edge[0]);
                    }
                }
                return true;

            }
            else if (numEdges() == 1)
            {
                clearEdges();
                return true;
            }
        }

        return false;
    }

    @Override
    public long[] getVelocity()
    {
        return null;
    }

    protected void clearEdges()
    {
        for (int i = 0; i < 6; ++i)
        {
            PipeVertex adj = getAdjacent(i);
            if (adj == null) continue;

            for (int j = 0; j < 6; ++j)
            {
                if (adj.getAdjVertex(j) == this)
                {
                    adj.setAdjVertex(j, null);
                }
            }

            setAdjVertex(i, null);
        }
    }
}
