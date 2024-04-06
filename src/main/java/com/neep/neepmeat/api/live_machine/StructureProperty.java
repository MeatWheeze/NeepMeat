package com.neep.neepmeat.api.live_machine;

import com.google.common.util.concurrent.AtomicDouble;

public enum StructureProperty
{
    FAULT_TOLERANCE(0),
    SPEED(1),
    SELF_REPAIR(0),
    MAX_POWER(0),
    MASS(1),
    ;

    private final float def;

    StructureProperty(float def)
    {
        this.def = def;
    }

    public float defaultValue()
    {
        return def;
    }

    public enum Function
    {
        ADD,
        AVERAGE;

        public boolean average()
        {
            return this == AVERAGE;
        }
    }

    public record Entry(Function function, float value)
    {
        public Entry(float value)
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
    }
}
