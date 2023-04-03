package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.data.FluidNetworkManager;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public interface PipeNetwork
{
    // My pet memory leak.
    Set<PipeNetwork> LOADED_NETWORKS = new CopyOnWriteArraySet<>();

    static void tryCreateNetwork(ServerWorld world, BlockPos pos)
    {
//        System.out.println("trying fluid network at " + pos);
        UUID uuid = UUID.randomUUID();
//        PipeNetworkImpl1 network = new PipeNetworkImpl1(world, uuid, pos);
        PipeNetwork network = new PipeNetworkImpl(world, uuid);
        network.rebuild(pos);
        if (network.isValid())
        {
            LOADED_NETWORKS.add(network);
            return;
        }
        else
        {
            network.remove();
        }
        System.out.println("fluid network failed");
    }

    static void createFromNbt(ServerWorld world, UUID uuid)
    {
        FluidNetworkManager manager = ((IServerWorld) world).getFluidNetworkManager();
        NbtCompound networkNbt = manager.getNetwork(uuid);

        // If the world didn't save properly, this could be null.
        if (networkNbt == null) return;

        PipeNetwork network = PipeNetworkImpl.fromNbt(world, networkNbt);
        LOADED_NETWORKS.add(network);
    }

    static void addNetwork(PipeNetwork network)
    {
        LOADED_NETWORKS.add(network);
    }

    static void removeNetwork(PipeNetwork network)
    {
        LOADED_NETWORKS.remove(network);
    }

    void rebuild(BlockPos startPos);
    void tick();

    static void registerEvent()
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
        {
            LOADED_NETWORKS.clear();
        });

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) ->
        {
//            ((IServerWorld) world).getFluidNetworkManager().getNetwork().
        });
    }

    boolean merge(BlockPos pos, PipeNetwork other);

    boolean isValid();
    UUID getUUID();
    boolean canTick(ServerWorld world);

    void update(BlockPos vertexPos, @Nullable PipeVertex vertex, UpdateReason reason);

    void remove();

    NbtCompound toNbt();

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
