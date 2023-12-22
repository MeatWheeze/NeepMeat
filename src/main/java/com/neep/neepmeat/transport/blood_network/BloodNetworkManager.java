package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class BloodNetworkManager extends PersistentState
{
    public static final String NAME = NeepMeat.NAMESPACE + "blood_networks";

    // Just the loaded networks.
    private final ArrayList<NetworkEntry> tickingNetworks = Lists.newArrayList();

    // All networks, loaded or unloaded.
    private final Map<UUID, NbtCompound> savedNetworks = Maps.newHashMap();

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

        long chunk = ChunkPos.toLong(pos);

        Set<UUID> adjacent = getOrCreateEntry(chunk);
        tickingNetworks.add(new NetworkEntry(network, adjacent));
        savedNetworks.put(network.getUUID(), network.toNbt());
        adjacent.add(network.uuid);

        return network;
    }

    protected void remove(NetworkEntry entry)
    {
        entry.container.remove(entry.uuid());
        savedNetworks.remove(entry.uuid());
    }

    private void tick()
    {
        var it = tickingNetworks.iterator();
        while (it.hasNext())
        {
            var entry = it.next();
            var network = entry.network();

            if (network.isRemoved())
            {
                it.remove();
                remove(entry);
            }
            else
            {
                network.tick();

                if (network.isDirty())
                {
                    savedNetworks.put(network.getUUID(), network.toNbt());
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

    public NbtCompound getNetwork(UUID uuid)
    {
        return savedNetworks.get(uuid);
    }

    protected void readNbt(NbtCompound nbt)
    {
        NbtList list = nbt.getList("networks", NbtCompound.COMPOUND_TYPE);

        for (var entry : list)
        {
            if (entry instanceof NbtCompound compound)
            {
                var uuid = compound.getUuid("uuid");
                savedNetworks.put(uuid, compound);
            }
            else throw new IllegalArgumentException();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        savedNetworks.forEach(((uuid, compound) ->
        {
            list.add(compound);
            compound.putUuid("uuid", uuid);
        }));

        nbt.put("networks", list);
        return nbt;
    }


    Long2ObjectOpenHashMap<Set<UUID>> chonks = new Long2ObjectOpenHashMap<>();

    Set<UUID> getOrCreateEntry(long chunk)
    {
        return chonks.computeIfAbsent(chunk, l -> Sets.newHashSet());
    }

    static
    {
        ServerTickEvents.END_WORLD_TICK.register(world ->
        {
            ((IServerWorld) world).getBloodNetworkManager().tick();
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) ->
        {
            var manager = get(world);
            var set = manager.chonks.get(chunk.getPos().toLong());
            if (set != null) set.forEach(uuid ->
            {
//                manager.tickingNetworks.remove
            });
        });

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) ->
        {
            var manager = get(world);
            var set = manager.chonks.get(chunk.getPos().toLong());

            if (set != null) set.forEach(uuid ->
            {
                var nbt = manager.savedNetworks.get(uuid);

                if (nbt == null) return;

                var network = BloodNetworkImpl.fromNbt(world, uuid, nbt);
                manager.tickingNetworks.add(new NetworkEntry(network, set));
                set.add(uuid);
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
