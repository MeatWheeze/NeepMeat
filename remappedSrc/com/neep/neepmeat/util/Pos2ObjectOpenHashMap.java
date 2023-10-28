package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;

public class Pos2ObjectOpenHashMap<V> extends Long2ObjectOpenHashMap<V>
{
    public V get(BlockPos pos)
    {
        return super.get(pos.asLong());
    }

    public V put(BlockPos pos, V v)
    {
        return super.put(pos.asLong(), v);
    }

    public V putIfAbsent(BlockPos pos, V v)
    {
        return super.putIfAbsent(pos.asLong(), v);
    }

    public V getOrDefault(BlockPos pos, V defaultValue)
    {
        return super.getOrDefault(pos.asLong(), defaultValue);
    }
}
