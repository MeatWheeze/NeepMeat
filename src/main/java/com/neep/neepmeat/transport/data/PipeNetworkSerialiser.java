package com.neep.neepmeat.transport.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
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
