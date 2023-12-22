package com.neep.neepmeat.transport.fluid_network;

public interface PipeVertex
{
    void tick();

    long receiveFluid(long amount);

    void putAdjacent(int dir, PipeVertex vertex);

    PipeVertex[] getAdjacentVertices();

    float getTotalHead();

    void setNetwork(PipeNetwork network);

    PipeNetwork getNetwork();

    void setElevationHead(float value);

    boolean canSimplify();

    void reset();
}
