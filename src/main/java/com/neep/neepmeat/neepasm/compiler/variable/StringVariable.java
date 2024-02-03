package com.neep.neepmeat.neepasm.compiler.variable;

public record StringVariable(String value) implements Variable<String>
{
    @Override
    public Class<String> type()
    {
        return String.class;
    }

    @Override
    public int compare(Variable<?> v2)
    {
        if (v2 instanceof StringVariable sv2)
        {
            return value.equals(sv2.value) ? 0 : -2;
        }
        return -2;
    }
}
