package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public interface PipeNetwork
{
    // My pet memory leak.
    Set<PipeNetwork> LOADED_NETWORKS = new CopyOnWriteArraySet<>();

    static Optional<PipeNetwork> tryCreateNetwork(ServerWorld world, BlockPos pos)
    {
//        System.out.println("trying fluid network at " + pos);
        UUID uuid = UUID.randomUUID();
//        PipeNetworkImpl1 network = new PipeNetworkImpl1(world, uuid, pos);
        PipeNetwork network = new PipeNetworkImpl2(world, uuid, pos);
        network.rebuild(pos);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return Optional.of(network);
        }
        System.out.println("fluid network failed");
        return Optional.empty();
    }

    void rebuild(BlockPos startPos);
    void tick();

    static void registerEvent()
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }

    boolean isValid();
    UUID getUUID();
    boolean canTick(ServerWorld world);

    void update(BlockPos vertexPos, @Nullable PipeVertex vertex, UpdateReason reason);

    void remove();

    enum UpdateReason
    {
        PIPE_REMOVED(true, false),
        PIPE_ADDED(false, true),
        CONNECTION_CHANGED(false, false),
        NODE_CHANGED(false, false),
        VALVE_CHANGED(false, false);

        private final boolean removed;
        private final boolean newPart;

        public boolean isRemoved()
        {
            return removed;
        }

        public boolean isNewPart() {return newPart; }

        UpdateReason(boolean removed, boolean newPart)
        {
            this.removed = removed;
            this.newPart = newPart;
        }
    }

    BlockApiLookup<PipeNetwork, Void> LOOKUP = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "fluid_network"), PipeNetwork.class, Void.class);
}
