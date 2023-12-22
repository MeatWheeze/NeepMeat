package com.neep.neepmeat.util;

import java.util.ArrayList;
import java.util.HashMap;

public class IndexedHashMap<K, V> extends ArrayList<V>
{
    protected HashMap<K, Integer> map;

    public IndexedHashMap()
    {
        this.map = new HashMap<>();
    }

    public V put(K k, V v)
    {
        if (map.get(k) == null)
        {
            if (this.add(v))
            {
                map.put(k, this.indexOf(v));
                return v;
            }
        }
        else
        {
            this.remove(map.get(k));
            map.remove(k);
            if (this.add(v))
            {
                map.put(k, this.indexOf(v));
                return v;
            }
        }
        return null;
    }

    public V get(int i)
    {
        return super.get(i);
    }

    public V get(K k)
    {
        return this.get(map.get(k));
    }
}
