package com.neep.neepmeat.transport.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PipeNetworkSerialiser extends PersistentState
{
    private static final String PIPES = "pipes";
    protected final ServerWorld world;

    // Contains networks that are currently ticking and networks that are stored.
    protected final Map<UUID, NbtCompound> allNetworks = new HashMap<>();

    protected final Multimap<Long, UUID> unloaded = HashMultimap.create();

    public PipeNetworkSerialiser(ServerWorld world)
    {
        this.world = world;
        this.markDirty();

//        ServerChunkEvents.CHUNK_LOAD.register((world1, chunk) ->
//        {
//            if (Objects.equals(world1, world))
//            {
//                unloaded.get(chunk.getPos().toLong())
//                        .forEach(uuid -> PipeNetwork.retrieveNetwork(world, allNetworks.get(uuid)));
//            }
//        });
    }

    public void storeNetwork(UUID uuid, NbtCompound nbt)
    {
        allNetworks.put(uuid, nbt);
        this.markDirty();
    }

    public void unloadNetwork(BlockPos from, PipeNetwork network)
    {
        unloaded.put(ChunkPos.toLong(from), network.getUUID());
    }

    public NbtCompound getNetwork(UUID uuid)
    {
        return allNetworks.get(uuid);
    }

    // Call when a network expands into a new chunk
    public void registerNetwork(PipeNetwork network)
    {
        allNetworks.put(network.getUUID(), network.toNbt());
//        chunkToNetwork.put(chunkPos, network);
    }

    public void unregisterNetwork(PipeNetwork network)
    {
        allNetworks.remove(network.getUUID());
    }

    // Methods for sorting the NBT contents into a UUID to compound list
    protected void readNetworks(NbtCompound nbt)
    {
        NbtList list = nbt.getList("networks", 10);
        list.forEach(e ->
        {
            UUID uuid = ((NbtCompound) e).getUuid("uuid");
            allNetworks.put(uuid, ((NbtCompound) e));
        });
    }

    protected void writeNetworks(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        allNetworks.forEach(((uuid, compound) ->
        {
            list.add(compound);
        }));
        nbt.put("networks", list);
    }

    public static void init()
    {
        ServerWorldEvents.LOAD.register((server, worldIn) ->
        {
            ((IServerWorld) worldIn).setFluidNetworkManager(worldIn.getPersistentStateManager().getOrCreate(nbt -> fromNbt(worldIn, nbt), () -> new PipeNetworkSerialiser(worldIn), PIPES));
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        writeNetworks(nbt);
        return nbt;
    }

    public static PipeNetworkSerialiser fromNbt(ServerWorld world, NbtCompound nbt)
    {
        PipeNetworkSerialiser manager = new PipeNetworkSerialiser(world);
        manager.readNetworks(nbt);
        return manager;
    }
}
