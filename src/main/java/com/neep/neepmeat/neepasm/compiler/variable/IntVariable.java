package com.neep.neepmeat.neepasm.compiler.variable;

public record IntVariable(Integer value) implements Variable<Integer>
{
    @Override
    public Class<Integer> type()
    {
        return Integer.class;
    }
}
