package com.neep.neepmeat.transport.fluid_network;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface PipeVertex
{
    void tick();

    long receiveFluid(long amount);

    void putAdjacent(int dir, PipeVertex vertex);

    PipeVertex[] getAdjacentVertices();

    boolean canFluidFlow(Direction connection, BlockState state);

    boolean isSpecial();

    ISpecialPipe getSpecial();

    boolean flag();

    void setFlag(boolean value);

    Direction[] getConnections();
}
