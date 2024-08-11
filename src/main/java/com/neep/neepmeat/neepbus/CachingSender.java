package com.neep.neepmeat.neepbus;

import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.BFSGroupFinder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CachingSender // Not a name that describes what this is, change.
{
    private final World world;
    private final BlockPos pos;

//    private final Multimap<String, NeepBusPort> portCache = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

    // Values can be null, empty, or full. Empty indicates that the search has already been run but the
    // target address is not present in the network.
    private final Map<String, Set<NeepBusPort>> portCache = new HashMap<>();

    public CachingSender(World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;
    }

    public void send(String address, int data)
    {
        @Nullable Set<NeepBusPort> cached = portCache.get(address);
        if (cached == null)
        {
            Finder finder = new Finder(address);
            finder.queueBlock(pos);
            finder.loop(32);

            cached = new HashSet<>(finder.getResult().values());
            portCache.put(address, cached);
        }

        for (var port : cached)
        {
            port.receive(data);
        }
    }

    public void clear()
    {
        portCache.clear();
    }

    private class Finder extends BFSGroupFinder<NeepBusPort>
    {
        private final String address;

        public Finder(String address)
        {
            this.address = address;
        }

        @Override
        protected State processPos(BlockPos pos)
        {
            BlockState currentState = world.getBlockState(pos);
            BlockPos.Mutable mutable = pos.mutableCopy();

            for (Direction direction : Direction.values())
            {
                mutable.set(pos, direction);
                if (visited.contains(mutable.asLong()))
                    continue;

                if (currentState.getBlock() instanceof DataCable dataCable)
                {
                    if (dataCable.isConnected(world, mutable, currentState, direction))
                    {
                        BlockState offsetState = world.getBlockState(mutable);

                        if (offsetState.isAir())
                            continue;

                        if (offsetState.getBlock() instanceof DataCable)
                        {
                            queueBlock(mutable.toImmutable());
                        }

                        if (offsetState.getBlock() instanceof NeepBusProvider provider)
                        {
                            NeepBusPort port = provider.getPorts(world, mutable, offsetState).get(address);
                            if (port != null)
                            {
                                addResult(mutable, port);
                            }
                        }
                    }
                }
            }

            return State.CONTINUE;
        }
    }
}
