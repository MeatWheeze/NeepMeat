package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Queues;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.TransportComponents;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class BloodNetworkManager extends PersistentState
{
    public static final String NAME = NeepMeat.NAMESPACE + "blood_networks";

    private final Object2ObjectMap<UUID, BloodNetwork> tickingNetworks = new Object2ObjectOpenHashMap<>();
//    private final ArrayList<NetworkEntry> tickingNetworks = Lists.newArrayList();

    private final ServerWorld world;

    public BloodNetworkManager(ServerWorld world)
    {
        this.world = world;
    }

    public BloodNetworkManager(ServerWorld world, NbtCompound nbt)
    {
        this.world = world;
        readNbt(nbt);
    }

    public BloodNetwork create(BlockPos pos)
    {
        var network = new BloodNetworkImpl(UUID.randomUUID(), world);

//        TransportComponents.BLOOD_NETWORK.get(world.getChunk(pos)).addNetwork(network);
        tickingNetworks.put(network.getUUID(), network);


//        long chunk = ChunkPos.toLong(pos);

//        Set<UUID> adjacent = getOrCreateEntry(chunk);
//        tickingNetworks.add(new NetworkEntry(network, adjacent));
//        savedNetworks.put(network.getUUID(), network.toNbt());
//        adjacent.add(network.uuid);

        return network;
    }

    private void tick()
    {
        var it = tickingNetworks.entrySet().iterator();
        while (it.hasNext())
        {
            var network = it.next().getValue();
//            var network = entrynetwork();

            if (network.isRemoved())
            {
                it.remove();
            }
            else
            {
                network.tick();

                if (network.isDirty())
                {
//                    savedNetworks.put(network.getUUID(), network.toNbt());
                    network.resetDirty();
                }
            }
        }
    }

    public static BloodNetworkManager get(World world)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            return ((IServerWorld) serverWorld).getBloodNetworkManager();
        }
        throw new IllegalArgumentException("Blood network manager must only be used on the logical server.");
    }

    protected void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    private static final Queue<Runnable> STAGED_EVENTS = Queues.newArrayDeque();

    public static void stageEvent(Runnable runnable)
    {
        STAGED_EVENTS.add(runnable);
    }

    static
    {
        ServerTickEvents.START_SERVER_TICK.register(server ->
        {
            while (!STAGED_EVENTS.isEmpty())
            {
                var event = STAGED_EVENTS.poll();
                event.run();
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world ->
        {
            get(world).tick();
        });

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) ->
        {
            var manager = get(world);
            var map = TransportComponents.BLOOD_NETWORK.get(chunk).getPipes().asMap();
            map.forEach((uuid, pipes) ->
            {
                stageEvent(() ->
                {
                    manager.tickingNetworks.computeIfAbsent(uuid, u -> new BloodNetworkImpl(uuid, world))
                            .insert(pipes);
                });
            });
        });
    }

    record NetworkEntry(BloodNetwork network, Set<UUID> container)
    {
        public UUID uuid()
        {
            return network.getUUID();
        }
    }
}
