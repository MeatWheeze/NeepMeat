package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Lists;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BloodNetworkManager
{
    private final ArrayList<BloodNetworkImpl> networks = Lists.newArrayList();
    private final ServerWorld world;

    public BloodNetworkManager(ServerWorld world)
    {
        this.world = world;
    }

    public BloodNetwork create(BlockPos pos)
    {
        var network = new BloodNetworkImpl(world);
        networks.add(network);
        return network;
    }

    private void tick()
    {
        networks.forEach(BloodNetworkImpl::tick);
        networks.removeIf(BloodNetworkImpl::isRemoved);
    }

    public static BloodNetworkManager get(World world)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            return ((IServerWorld) serverWorld).getBloodNetworkManager();
        }
        throw new IllegalArgumentException("Blood network managers must only be used on the logical server.");
    }

    static
    {
        ServerTickEvents.END_WORLD_TICK.register(world ->
        {
            ((IServerWorld) world).getBloodNetworkManager().tick();
        });
    }
}
