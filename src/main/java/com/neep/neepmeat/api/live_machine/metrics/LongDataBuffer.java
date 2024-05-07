package com.neep.neepmeat.api.live_machine.metrics;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public class LongDataBuffer extends LongArrayFIFOQueue
{
    private final int capacity;

    public LongDataBuffer(int capacity)
    {
        super(capacity);
        this.capacity = capacity;
    }

    public void enqueueCircular(long f)
    {
        enqueue(f);
        if (size() >= capacity)
            dequeueLong();
    }

    public long[] encode()
    {
        LongArrayList longs = new LongArrayList();

        int i = start;
        while (true)
        {
            // end is always shorter than length
            if (i == array.length)
                i = 0;

            if (i == end)
                break;

            longs.add(array[i]);
            i++;

        }
        return longs.elements();
    }
}
