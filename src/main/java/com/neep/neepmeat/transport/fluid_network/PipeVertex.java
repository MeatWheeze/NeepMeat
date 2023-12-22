package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface PipeVertex
{
    void tick();

    long insert(int fromDir, int toDir, long amount, TransactionContext transaction);

    PipeVertex[] getAdjVertices();

    default PipeVertex getAdjVertex(int dir)
    {
        return getAdjVertices()[dir];
    }

    default void setAdjVertex(int dir, PipeVertex vertex)
    {
        getAdjVertices()[dir] = vertex;
    }

    float getTotalHead();

    void setNetwork(PipeNetwork network);

    PipeNetwork getNetwork();

    void setElevationHead(float value);

    boolean canSimplify();

    void reset();

    boolean collapseEdges();
}
