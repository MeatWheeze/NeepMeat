package com.neep.neepmeat.api.live_machine;

import com.google.common.util.concurrent.AtomicDouble;

public record PropertyValue(Function function, float value)
{
    public PropertyValue(float value)
    {
        this(Function.AVERAGE, value);
    }

    public void apply(AtomicDouble atomicDouble, int count)
    {
        switch (function)
        {
            case ADD -> atomicDouble.addAndGet(value);
            case AVERAGE -> atomicDouble.addAndGet(value / count);
        }
    }

    public float apply(float f, int count)
    {
        return switch (function)
        {
            case ADD -> f + value;
            case AVERAGE -> f + (value / count);
        };
    }

    public enum Function
    {
        ADD("added"),
        AVERAGE("averaged");

        public final String name;

        Function(String name)
        {
            this.name = name;
        }

        public boolean average()
        {
            return this == AVERAGE;
        }
    }
}
