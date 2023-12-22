package com.neep.neepmeat.transport.item_network;

import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.util.DFSFinder;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RoutingNetworkDFSFinder extends DFSFinder<BlockApiCache<RoutingNetwork, Void>>
{
    private final World world;

    public RoutingNetworkDFSFinder(World world)
    {
        this.world = world;
    }

    @Override
    protected State processPos(BlockPos current, Direction fromDir)
    {
        ItemPipe fromPipe = ItemTransport.ITEM_PIPE.find(world, current, fromDir);

        BlockApiCache<RoutingNetwork, Void> cache = BlockApiCache.create(RoutingNetwork.LOOKUP, (ServerWorld) world, current);
        if (cache.find(null) != null)
        {
            setResult(current, cache);
            return State.SUCCESS;
        }

        BlockPos.Mutable mutable = current.mutableCopy();
        for (Direction direction : fromPipe.getConnections(world.getBlockState(current), d -> true))
        {
            mutable.set(current, direction);
            if (visited(mutable)) continue;

            ItemPipe offsetPipe = ItemTransport.ITEM_PIPE.find(world, mutable, direction);
            if (offsetPipe != null)
            {
                setVisited(mutable);
                pushBlock(mutable, direction);
                return State.CONTINUE;
            }
        }

        popBlock();
        popDir();

        return State.CONTINUE;
    }

    public static RoutingNetwork getDriver(World world, BlockPos pos)
    {
        return RoutingNetwork.LOOKUP.find(world, pos, null);
//        return world.getBlockEntity(pos, ItemTransport.PIPE_DRIVER_BE).orElse(null);
    }
}
