package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface PipeVertex extends PipeFlowComponent
{
    void tick();

    default void preTick() {}

    PipeVertex[] getAdjVertices();

    default PipeVertex getAdjVertex(int dir)
    {
        return getAdjVertices()[dir];
    }

    default void setAdjVertex(int dir, PipeVertex vertex)
    {
        getAdjVertices()[dir] = vertex;
    }

    void setNetwork(PipeNetwork network);

    PipeNetwork getNetwork();


    boolean canSimplify();

    void reset();

    boolean collapseEdges();

    long[] getVelocity();

    float getTotalHead();
    void setElevationHead(float value);
}
