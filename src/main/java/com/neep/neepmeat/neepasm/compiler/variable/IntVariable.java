package com.neep.neepmeat.neepasm.compiler.variable;

public record IntVariable(Integer value) implements Variable<Integer>
{
    @Override
    public Class<Integer> type()
    {
        return Integer.class;
    }

    @Override
    public int compare(Variable<?> v2)
    {
        if (v2 instanceof IntVariable iv2)
        {
            return value.compareTo(iv2.value);
        }
        return -2;
    }
}
