package com.neep.neepmeat.data;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.Main;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.PersistentState;

public class FluidNetworkManager extends PersistentState
{
    private static final String PIPES = "pipes";
    protected ServerWorld world;

    public FluidNetworkManager(ServerWorld world)
    {
        this.world = world;
        this.markDirty();
    }

    public static void init()
    {
        ServerWorldEvents.LOAD.register((server, world1) ->
        {
//            world1.getPersistentStateManager().getOrCreate(nbt -> fromNbt(world1, nbt), () -> new FluidNetworkManager(world1), PIPES);
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return null;
    }

    public static FluidNetworkManager fromNbt(ServerWorld world, NbtCompound nbt)
    {
        FluidNetworkManager manager = new FluidNetworkManager(world);

        return manager;
    }
}
