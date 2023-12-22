package com.neep.neepmeat.transport.fluid_network;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PipeNetworkImpl2 implements PipeNetwork
{
    protected final PipeNetGraph graph;
    protected final ServerWorld world;
    protected final UUID uuid;
    protected boolean removed;

    public PipeNetworkImpl2(ServerWorld world, UUID uuid, BlockPos pos)
    {
        this.world = world;
        this.graph = new PipeNetGraph(world);
        this.uuid = uuid;
    }

    public void rebuild(BlockPos startPos)
    {
        // TODO: feature envy
        graph.allVertices.long2ObjectEntrySet().fastForEach(e -> e.getValue().reset());
        graph.rebuild(startPos);
        graph.allVertices.long2ObjectEntrySet().fastForEach(e ->
        {
            e.getValue().setNetwork(this);
        });
        graph.minimiseGraph();
        graph.calculateHead();
    }

    @Override
    public void tick()
    {
        if (removed) return; // This indicates a problem. TODO: throw an exception

        validate();
        graph.getVertices().long2ObjectEntrySet().fastForEach(e -> e.getValue().tick());
    }

    protected void validate()
    {
        if (!isValid())
        {
            remove();
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

    @Override
    public void update(BlockPos vertexPos, @Nullable PipeVertex vertex, UpdateReason reason)
    {
        if (reason.isRemoved())
        {
//            if (vertex == null) vertex = graph.getVertex(vertexPos);
//
//            for (Direction direction : Direction.values())
//            {
//                PipeVertex adj = vertex.getAdjacentVertices()[direction.ordinal()];
//                if (adj == null) continue;
//
//                PipeNetwork network = adj.getNetwork();
//                BlockPos nextPos = vertexPos.offset(direction);
//                if (network != null)
//                {
//                    network.rebuild(nextPos);
//                }
//            }
            rebuild(vertexPos);
        }

        if (reason.isNewPart())
        {
            rebuild(vertexPos);
        }

        validate();
    }

    @Override
    public void remove()
    {
        removed = true;
        graph.allVertices.long2ObjectEntrySet().fastForEach(e -> e.getValue().setNetwork(null));
        PipeNetwork.LOADED_NETWORKS.remove(this);
    }

    public PipeNetGraph getGraph()
    {
        return graph;
    }
}
