package com.neep.neepmeat.api.processing.random_ores;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedMap<T>
{
    private final NavigableMap<Float, T> entryMap = new TreeMap<>();
    private float weightTotal = 0;

    public void put(float weight, T entry)
    {
        weightTotal += weight;
        entryMap.put(weightTotal, entry);
    }

    public T get(Int2IntFunction random, float p)
    {
        if (entryMap.size() == 0)
            return null;

        if (weightTotal == -1)
        {
            weightTotal = 0;
            for (var entry : entryMap.keySet())
            {
                weightTotal += entry;
            }
        }

        p *= weightTotal;

        var mapEntry = entryMap.higherEntry(p);

        return mapEntry.getValue();
    }

    public void clear()
    {
        entryMap.clear();
        weightTotal = 0;
    }
}
