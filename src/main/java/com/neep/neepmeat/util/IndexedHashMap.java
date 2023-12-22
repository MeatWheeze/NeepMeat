package com.neep.neepmeat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexedHashMap<K, V> implements Cloneable
{
    // Tests indicated that subscript iteration through an ArrayList can be 1/3000 times faster than iterating through a Map's entry set.
    // This was an attempt to have a Map-like thing that provided subscript iteration. There is probably a better solution
    // that I am unaware of.

    protected HashMap<K, V> map;
    protected ArrayList<V> values;
    protected ArrayList<K> keys;

    public IndexedHashMap()
    {
        this.map = new HashMap<>(); // Speedy lookup
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public V put(K k, V v)
    {
//        if (keys.indexOf(v) == -1)
        if (map.get(k) == null && !keys.contains(k)) // No entry exists. This hopefully means that it's not present in values or keys.
        {
            values.add(v);
            keys.add(k);
            int index = values.indexOf(v);
            map.put(k, v);
            return v;
        }
//        else
//        {
//            int index = map.get(k);
//            values.remove(index);
//            keys.remove(index);
//            map.remove(k);
//            if (values.add(v))
//            {
//                map.put(k, values.indexOf(v));
//                return v;
//            }
//        }
        return null;
    }

    public void remove(K k)
    {
//        int index = map.get(k);
        int index = keys.indexOf(k);
        keys.remove(index);
        values.remove(index);
        map.remove(k);

        // Shift all values beyond the removed index
//        map.replaceAll((k1, v1) ->
//        {
//            if (v1 > index)
//            {
//                return v1 - 1;
//            }
//            return v1;
//        });
//        map.remove(k, index);
    }

    public int size()
    {
        return values.size();
    }

    public void clear()
    {
        keys.clear();
        values.clear();
        map.clear();
    }

    public List<K> keySet()
    {
        return keys;
    }

    public V get(int i)
    {
        return values.get(i);
    }

    public V get(K k)
    {
//        Integer index = map.get(k);
//        if (index != null)
//            return this.get(index);
//
//        return null;
        return map.get(k);
    }

    public K getKey(int i)
    {
        return keys.get(i);
    }

    public int getIndex(K k)
    {
//        return map.get(k);
        return keys.indexOf(k);
    }

    @Override
    public IndexedHashMap<K, V> clone()
    {
        try
        {
            IndexedHashMap<K, V> clone = (IndexedHashMap<K, V>) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e)
        {
            throw new AssertionError();
        }
    }

    @Override
    public String toString()
    {
        return keys.toString() + "\n" + values.toString() + "\n" + map.toString();
    }
}
