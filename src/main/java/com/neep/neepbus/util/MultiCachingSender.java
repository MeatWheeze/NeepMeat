package com.neep.neepbus.util;

import com.neep.neepbus.block.NeepBusProvider;
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
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * For use when there are multiple known output ports that will be used regularly.
 */
public class MultiCachingSender
{
    private final Supplier<World> world;
    private final BlockPos pos;
    private final Predicate<String> addressPredicate;

    @Nullable private Map<String, Set<WritePort>> portCache;

    public MultiCachingSender(Supplier<World> world, BlockPos pos, Predicate<String> addressPredicate)
    {
        this.world = world;
        this.pos = pos;
        this.addressPredicate = addressPredicate;
    }

    public void send(String address, int data)
    {
        if (portCache == null)
        {
            Finder<WritePort> finder = new Finder<>(false);
            finder.queueBlock(pos);
            finder.loop(32);

            portCache = finder.result;
//            portCache.forEach((s, neepBusPorts) -> neepBusPorts.forEach(p -> p.addInvalidateListener(this::invalidate)));
        }

        // If an address is not in the map, a corresponding receiver was not found.
        @Nullable Set<WritePort> ports = portCache.get(address);
        if (ports != null)
        {
            for (var port : portCache.get(address))
            {
                port.write(data);
            }
        }
    }
    
    // TODO: read

    public void invalidate()
    {
        portCache = null;
    }

    private class Finder<T extends NeepBusPort> extends BFSGroupFinder<T>
    {
        private final boolean read;
        private final Map<String, Set<T>> result = new HashMap<>();

        public Finder(boolean read)
        {
            this.read = read;
        }

        protected void addResult(String address, T port)
        {
            result.computeIfAbsent(address, s -> new HashSet<>()).add(port);
        }

        @Override
        protected State processPos(BlockPos pos)
        {
            World world = MultiCachingSender.this.world.get();
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
                            // JAAAAAAAAAAAAANK
                            if (read)
                            {
                                for (var entry : provider.getPorts(world, mutable, offsetState).readPorts().entrySet())
                                {
                                    if (addressPredicate.test(entry.getKey()))
                                        addResult(entry.getKey(), (T) entry.getValue());
                                }
                            }
                            else
                            {
                                for (var entry : provider.getPorts(world, mutable, offsetState).writePorts().entrySet())
                                {
                                    if (addressPredicate.test(entry.getKey()))
                                        addResult(entry.getKey(), (T) entry.getValue());
                                }
                            }
                        }
                    }
                }
            }

            return State.CONTINUE;
        }
    }
}
