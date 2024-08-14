package com.neep.neepbus.util;

import com.neep.neepbus.block.NeepBusProvider;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Queue;

public class NeepBusUtil
{
    public static String formatValue(int value)
    {
        return String.valueOf(value);
    }

    public static void updateImmediateNeighbours(World world, BlockPos pos, BlockState state)
    {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values())
        {
            mutable.set(pos, direction);

            if (world.getBlockState(mutable).getBlock() instanceof NeepBusProvider provider)
            {
                provider.networkChanged(world, mutable, pos);
            }
        }
    }

    public static void floodUpdate(World world, BlockPos pos)
    {
        // BFS #1381641

        LongSet visited = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();

        visited.add(pos.asLong());
        queue.add(pos);

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();

            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (!visited.contains(mutable.asLong()))
                {
                    visited.add(mutable.asLong());
                    BlockState offsetState = world.getBlockState(mutable);

                    if (offsetState.getBlock() instanceof DataCable)
                    {
                        queue.add(mutable.toImmutable());
                    }

                    if (offsetState.getBlock() instanceof NeepBusProvider provider)
                    {
                        provider.networkChanged(world, mutable, pos);
                    }
                }
            }
        }
    }
}
