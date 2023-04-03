package com.neep.neepmeat.transport.fluid_network;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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
        graph.getVertices().long2ObjectEntrySet().fastForEach(e -> e.getValue().preTick());
        graph.getVertices().long2ObjectEntrySet().fastForEach(e -> e.getValue().tick());
    }

    @Override
    public boolean merge(BlockPos pos, PipeNetwork other)
    {
        if (other == this)
        {
            rebuild(pos);
            return true;
        }

        if (other instanceof PipeNetworkImpl2 impl2)
        {
            impl2.remove();

            rebuild(pos);

            return true;
        }

        return false;
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
        // A network is definitely valid if there are two or more vertices.
        if (graph.getVertices().size() >= 2) return true;
        else
        {
            // If there is only one vertex, it can choose to keep the network valid.
            Long2ObjectMap.Entry<PipeVertex> vertexEntry = getGraph().getVertices().long2ObjectEntrySet().stream().findFirst().orElse(null);
            return vertexEntry != null && vertexEntry.getValue().keepNetworkValid();
        }
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
//        if (reason.isRemoved())
//        {
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
//            rebuild(vertexPos);
//        }

//        if (reason.isNewPart())
//        {
//            rebuild(vertexPos);
//        }

        if (reason == UpdateReason.VALVE_CHANGED) return;

        rebuild(vertexPos);
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
