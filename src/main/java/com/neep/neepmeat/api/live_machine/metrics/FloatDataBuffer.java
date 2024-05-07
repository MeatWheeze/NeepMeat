package com.neep.neepmeat.api.live_machine.metrics;

import it.unimi.dsi.fastutil.floats.FloatArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.stream.IntStream;

public class FloatDataBuffer extends FloatArrayFIFOQueue
{
    private final int capacity;

    public FloatDataBuffer(int capacity)
    {
        super(capacity);
        this.capacity = capacity;
    }

    public void enqueueCircular(float f)
    {
        enqueue(f);
        int s = size();
        if (size() >= capacity)
        {
            float f1 = dequeueFloat();
        }
    }

    public int[] encode()
    {
        IntArrayList ints = new IntArrayList();

        int i = start;
        while (i != end)
        {
            // end is always shorter than length
            if (i == array.length)
                i = 0;

            if (i == end)
                break;

            ints.add(Float.floatToIntBits(array[i]));
            i++;
        }
        return ints.elements();
    }
}
