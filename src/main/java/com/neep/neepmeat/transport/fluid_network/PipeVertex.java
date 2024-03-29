package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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

    boolean canSimplify();

    void tickDeferredLoad();

    void reset();

    void updateNodes(ServerWorld world, BlockPos pos, BlockState state);

    float getTotalHeight();
    void setHeight(float value);

    long getPos();

    float getPumpHeight();

    void erase();

    BlockApiLookup<PipeVertex, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "pipe_vertex"),
            PipeVertex.class, Void.class
    );
}
