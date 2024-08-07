package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * From <a href="https://github.com/lemire/Code-used-on-Daniel-Lemire-s-blog/blob/master/2017/09/18_2/VisitInDisorder.java">...</a>
 */
public class IterateRandomly implements IntIterable
{
    private final static int MAX_COUNT = 100000;

    private final int maxrange;
    private final int prime;

    private int index;
    private int runningvalue;

    public IterateRandomly(int range)
    {
        if (range < 2)
            throw new IllegalArgumentException("Your range needs to be greater than 1 " + range);

        int min = range / 2;

        maxrange = range;
        prime = selectCoPrimeResev(min, range);

        int offset = ThreadLocalRandom.current().nextInt(range);
        index = 0;
        runningvalue = offset;
    }

    static int selectCoPrimeResev(int min, int target)
    {
        int count = 0;
        int selected = 0;
        java.util.concurrent.ThreadLocalRandom rand = java.util.concurrent.ThreadLocalRandom.current();
        for (int val = min; val < target; ++val)
        {
            if (coprime(val, target))
            {
                count += 1;
                if ((count == 1) || (rand.nextInt(count) < 1))
                {
                    selected = val;
                }
            }
            if (count == MAX_COUNT) return val;
        }
        return selected;
    }

    static boolean coprime(int u, int v)
    {
        return greatestCoprime(u, v) == 1;
    }

    static int greatestCoprime(int u, int v)
    {
        int shift;
        if (u == 0) return v;
        if (v == 0) return u;
        for (shift = 0; ((u | v) & 1) == 0; ++shift)
        {
            u >>= 1;
            v >>= 1;
        }

        while ((u & 1) == 0)
            u >>= 1;

        do
        {
            while ((v & 1) == 0)
                v >>= 1;
            if (u > v)
            {
                int t = v;
                v = u;
                u = t;
            }
            v = v - u;
        }
        while (v != 0);
        return u << shift;
    }

    // computed the long way
//    private int getCurrentValue()
//    {
//        return (int) (((long) index * prime + offset) % (maxrange));
//        // the multiplication and the modulo could be optimized away with some work
//    }

    public boolean hasNext()
    {
        return index < maxrange;
    }

    public int next()
    {
        runningvalue += prime;
        if (runningvalue >= maxrange) runningvalue -= maxrange;
        index++;
        // next line can be safely uncommented
        //if(runningvalue != getCurrentValue()) throw new RuntimeException("bug");
        return runningvalue;
    }


    @Override
    public IntIterator iterator()
    {
        return new Iterator();
    }

    private class Iterator implements IntIterator
    {
        @Override
        public int nextInt()
        {
            return IterateRandomly.this.next();
        }

        @Override
        public boolean hasNext()
        {
            return IterateRandomly.this.hasNext();
        }
    }
}
