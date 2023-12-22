package com.neep.neepmeat.fluid_util.node;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class NodeDataManager extends PersistentState
{
    private final ServerWorld world;

    public static NodeDataManager getWorldInstance(ServerWorld world)
    {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(nbt -> fromNbt(world, nbt), () -> new NodeDataManager(world), NeepMeat.NAMESPACE + "_fluid_nodes");
    }

    public NodeDataManager(ServerWorld world)
    {
        this.world = world;
    }

    public static NodeDataManager fromNbt(ServerWorld world, NbtCompound nbt)
    {
        return new NodeDataManager(world);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {

    }
}
