package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.data.PipeNetworkSerialiser;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
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

@Deprecated
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
            startTickingNetwork(network);
        }
        else
        {
//        System.out.println("fluid network failed");
        }
    }


    static void startTickingNetwork(PipeNetwork network)
    {
        LOADED_NETWORKS.add(network);
    }

    static void stopTickingNetwork(PipeNetwork network)
    {
        network.unload();
        LOADED_NETWORKS.remove(network);
    }


    static void discardNetwork(ServerWorld world, PipeNetwork network)
    {
        LOADED_NETWORKS.remove(network);
        ((IServerWorld) world).getPipeNetworkManager().unregisterNetwork(network);
    }

    static void storeNetwork(ServerWorld world, PipeNetwork network)
    {
        ((IServerWorld) world).getPipeNetworkManager().storeNetwork(network.getUUID(), network.toNbt());
    }

    static void retrieveNetwork(ServerWorld world, UUID uuid)
    {
        PipeNetworkSerialiser manager = ((IServerWorld) world).getPipeNetworkManager();
        NbtCompound networkNbt = manager.getNetwork(uuid);

        if (networkNbt == null) return;

        PipeNetwork network = PipeNetworkImpl.fromNbt(world, networkNbt);
        startTickingNetwork(network);
    }

//    static void retrieveNetwork(ServerWorld world, NbtCompound nbt)
//    {
//        PipeNetwork network = PipeNetworkImpl.fromNbt(world, nbt);
//        startTickingNetwork(network);
//    }

    static void registerEvent()
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
        {
            LOADED_NETWORKS.clear();
        });
    }

    void rebuild(BlockPos startPos);
    void tick();

    boolean merge(BlockPos pos, PipeNetwork other);

    boolean isValid();
    UUID getUUID();
    boolean canTick(ServerWorld world);

    void update(BlockPos vertexPos, @Nullable PipeVertex vertex, UpdateReason reason);
    
    void remove();

    NbtCompound toNbt();

    void unload();

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
