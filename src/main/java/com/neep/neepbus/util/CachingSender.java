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
import java.util.function.Supplier;

public class CachingSender
{
    private final Supplier<World> world;
    private final BlockPos pos;

    // Values can be null, empty, or full. Empty indicates that the search has already been run but the
    // target address is not present in the network.
    private final Map<String, Set<WritePort>> writePortCache = new HashMap<>();
    private final Map<String, Set<ReadPort>> readPortCache = new HashMap<>();

    public CachingSender(Supplier<World> world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;
    }

    public void send(String address, int data)
    {
        @Nullable Set<WritePort> cached = writePortCache.get(address);
        if (cached == null)
        {
            Finder<WritePort> finder = new Finder<>(address, false);
            finder.queueBlock(pos);
            finder.loop(32);

            cached = new HashSet<>(finder.getResult().values());
//            cached.forEach(port -> port.addInvalidateListener(this::invalidate));
            writePortCache.put(address, cached);
        }

        for (var port : cached)
        {
            port.write(data);
        }
    }

    public int read(String address)
    {
        @Nullable Set<ReadPort> cached = readPortCache.get(address);
        if (cached == null)
        {
            Finder<ReadPort> finder = new Finder<>(address, true);
            finder.queueBlock(pos);
            finder.loop(32);

            cached = new HashSet<>(finder.getResult().values());
//            cached.forEach(port -> port.addInvalidateListener(this::invalidate));
            readPortCache.put(address, cached);
        }

        if (!cached.isEmpty())
        {
            return cached.iterator().next().read();
        }

        return 0;
    }

    public void invalidate()
    {
        writePortCache.clear();
    }

    private class Finder<T extends NeepBusPort> extends BFSGroupFinder<T>
    {
        private final String address;
        private final boolean read;

        public Finder(String address, boolean read)
        {
            this.address = address;
            this.read = read;
        }

        @Override
        protected State processPos(BlockPos pos)
        {
            World world = CachingSender.this.world.get();
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
                            NeepBusPort port = read ? provider.getPorts(world, mutable, offsetState).readPorts().get(address)
                                    : provider.getPorts(world, mutable, offsetState).writePorts().get(address);
                            if (port != null)
                            {
                                addResult(mutable, (T) port);
                            }
                        }
                    }
                }
            }

            return State.CONTINUE;
        }
    }
}
