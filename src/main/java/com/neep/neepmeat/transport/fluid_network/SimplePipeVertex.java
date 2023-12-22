package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.FluidTransport;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public abstract class SimplePipeVertex extends SnapshotParticipant<ResourceAmount<FluidVariant>> implements PipeVertex
{
//    protected final List<Direction> connections = new ArrayList<>();
    private final PipeVertex[] adjacentVertices = new PipeVertex[6];
    protected float pumpHeight = 0;
    protected float height;

    protected long amount;
    protected long oldAmount;

    protected final long pos;
    protected FluidVariant variant = FluidVariant.blank();

    public SimplePipeVertex(long pos)
    {
        this.pos = pos;
    }

    public long getCapacity()
    {
        return FluidTransport.MAX_TRANSFER;
    }

    public long getAmount()
    {
        return amount;
    }

    public FluidVariant getVariant()
    {
        return variant;
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot()
    {
        return new ResourceAmount<>(variant, amount);
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot)
    {
        this.variant = snapshot.resource();
        this.amount = snapshot.amount();
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

    public long insert(int fromDir, int toDir, long maxAmount, ServerWorld world, FluidVariant insertVariant, TransactionContext transaction)
    {
        long remaining = getCapacity() - amount;
        if ((!insertVariant.isBlank() && (variant.isBlank() || insertVariant.equals(variant))) && remaining > 0)
        {
            updateSnapshots(transaction);

            long permittedAmount = Math.min(canInsert(world, toDir, insertVariant, maxAmount), remaining);

            if (permittedAmount > 0)
            {
                this.amount += permittedAmount;
                variant = insertVariant;
                return permittedAmount;
            }
            return 0;
        }
        return 0;
    }

    @Override
    public int getConnectionDir(PipeVertex from)
    {
        for (int i = 0; i < adjacentVertices.length; ++i)
        {
            if (from.equals(adjacentVertices[i])) return i;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        StringBuilder adj = new StringBuilder();
        for (PipeVertex v : adjacentVertices)
        {
            if (v != null) adj.append(System.identityHashCode(v)).append(", ");
        }
        return "Vertex@"+System.identityHashCode(this)+"{connection=" + adj + "head:" + getTotalHeight() + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
//        if (o instanceof SimplePipeVertex vertex)
//        {
//            return Arrays.equals(adjacentVertices, vertex.getAdjVertices())
//                    && pressureHead == vertex.pressureHead
//                    && elevationHead == vertex.elevationHead
//                    && amount == vertex.amount;
//        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
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

    public void setHeight(float value)
    {
        this.height = value;
    }

    @Override
    public void addHead(int h)
    {
        pumpHeight += h;
    }

    @Override
    public long getPos()
    {
        return pos;
    }

    public float getTotalHeight()
    {
        return pumpHeight + getHeight();
    }

    protected float getHeight()
    {
        return height;
    }

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
        pumpHeight = 0;
        height = 0;
        clearEdges();
    }

    @Override
    public boolean collapseEdges()
    {
        if (canSimplify())
        {
            if (numEdges() == 2)
            {
                PipeVertex[] edge = new PipeVertex[2];
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

//    @Override
//    public long[] getVelocity()
//    {
//        return null;
//    }

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

    public long canInsert(ServerWorld world, int inDir, FluidVariant variant, long maxAmount)
    {
        return Math.min(maxAmount, getCapacity() - getAmount());
    }
}
