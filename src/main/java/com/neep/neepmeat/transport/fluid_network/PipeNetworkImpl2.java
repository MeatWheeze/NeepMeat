package com.neep.neepmeat.transport.fluid_network;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PipeNetworkImpl2 implements PipeNetwork
{
    protected final PipeNetGraph graph;
    protected final ServerWorld world;
    protected final UUID uuid;

    public PipeNetworkImpl2(ServerWorld world, UUID uuid, BlockPos pos)
    {
        this.world = world;
        this.graph = new PipeNetGraph(world);
        this.uuid = uuid;
    }

    public void rebuild(BlockPos startPos)
    {
        graph.rebuild(startPos);
    }

    @Override
    public void tick()
    {
        validate();
        graph.getVertices().long2ObjectEntrySet().fastForEach(e -> e.getValue().tick());
    }

    protected void validate()
    {
        if (!isValid())
        {
            PipeNetwork.LOADED_NETWORKS.remove(this);
        }
    }

    @Override
    public boolean isValid()
    {
        return graph.getVertices().size() >= 2;
    }

    @Override
    public UUID getUUID()
    {
        return uuid;
    }

    @Override
    public boolean canTick(ServerWorld world)
    {
        return this.world.equals(world);
    }

    public PipeNetGraph getGraph()
    {
        return graph;
    }
}
