package com.neep.neepmeat.transport.data;

import com.neep.neepmeat.transport.intrfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FluidNetworkManager extends PersistentState
{
    private static final String PIPES = "pipes";
    protected ServerWorld world;

    public FluidNetworkManager(ServerWorld world)
    {
        this.world = world;
        this.markDirty();
    }

    protected final Map<UUID, NbtCompound> unloaded = new HashMap<>();

    public void storeNetwork(UUID uuid, NbtCompound nbt)
    {
        unloaded.put(uuid, nbt);
        this.markDirty();
    }

    public NbtCompound getNetwork(UUID uuid)
    {
        return unloaded.get(uuid);
    }

    protected void readNetworks(NbtCompound nbt)
    {
        NbtList list = nbt.getList("networks", 10);
        list.forEach(e ->
        {
            UUID uuid = ((NbtCompound) e).getUuid("uuid");
            unloaded.put(uuid, ((NbtCompound) e));
        });
    }

    protected void writeNetworks(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        unloaded.forEach(((uuid, compound) ->
        {
            list.add(compound);
        }));
        nbt.put("networks", list);
    }

    public static void init()
    {
        ServerWorldEvents.LOAD.register((server, worldIn) ->
        {
            ((IServerWorld) worldIn).setFluidNetworkManager(worldIn.getPersistentStateManager().getOrCreate(nbt -> fromNbt(worldIn, nbt), () -> new FluidNetworkManager(worldIn), PIPES));
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        writeNetworks(nbt);
        return nbt;
    }

    public static FluidNetworkManager fromNbt(ServerWorld world, NbtCompound nbt)
    {
        FluidNetworkManager manager = new FluidNetworkManager(world);
        manager.readNetworks(nbt);
        return manager;
    }
}
