package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomMap<K, V>
{
    protected Object2ObjectMap<K, V> byId = new Object2ObjectOpenHashMap<>();
    protected NavigableMap<Float, V> byWeight = new TreeMap<>();
    protected Random random = new Random();

    protected float totalWeight;

    public void put(K k, V v, float weight)
    {
        totalWeight += weight;
        byId.put(k, v);
        byWeight.put(totalWeight, v);
    }

    public V get(K k)
    {
        return byId.get(k);
    }

    public V next()
    {
        float value = random.nextFloat() * totalWeight;
        return byWeight.higherEntry(value).getValue();
    }

    public Object2ObjectMap<K, V> getById()
    {
        return byId;
    }
}
