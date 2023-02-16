package com.neep.neepmeat.transport.item_network;

import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RoutingNetworkCache
{
    protected BlockApiCache<RoutingNetwork, Void> networkCache;
    protected long networkVersion;

    public void update()
    {
        if (networkCache != null)
        {
            RoutingNetwork network = networkCache.find(null);
            if (network != null && network.getVersion() == this.networkVersion)
            {
                network.invalidate();
            }
        }
    }

    public boolean isValid()
    {
        RoutingNetwork network = networkCache.find(null);
        if (network != null)
        {
            return network.getVersion() == networkVersion;
        }
        return true;
    }

    public void setNetwork(ServerWorld world, BlockPos pos)
    {
        this.networkCache = BlockApiCache.create(RoutingNetwork.LOOKUP, world, pos);
        RoutingNetwork network = networkCache.find(null);
        if (network != null) this.networkVersion = network.getVersion();
    }
}
