package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Long2ObjectMultimap<T>
{
    private final Long2ObjectOpenHashMap<Set<T>> map = new Long2ObjectOpenHashMap<>();
    private List<T> listCache;
    boolean dirty = true;

    public Long2ObjectMultimap()
    {
        map.defaultReturnValue(Collections.emptySet());
    }

    public void fastForEach(Consumer<Long2ObjectMap.Entry<Set<T>>> consumer)
    {
        map.long2ObjectEntrySet().fastForEach(consumer);
    }

    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public void remove(long key)
    {
        map.remove(key);
        dirty = true;
    }

    public Collection<T> get(long key)
    {
        return Collections.unmodifiableSet(map.computeIfAbsent(key, k -> Sets.newHashSet()));
    }

    public void clear()
    {
        map.clear();
        dirty = true;
    }

    public Stream<T> flatStream()
    {
        return map.values().stream().flatMap(Set::stream);
    }

    public void put(long key, T value)
    {
        map.computeIfAbsent(key, k -> Sets.newHashSet()).add(value);
        dirty = true;
    }

    public List<T> asList()
    {
        if (dirty || listCache == null)
        {
            listCache = flatStream().toList();
        }
        return listCache;
    }
}
