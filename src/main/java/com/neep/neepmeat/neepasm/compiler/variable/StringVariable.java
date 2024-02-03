package com.neep.neepmeat.neepasm.compiler.variable;

public record StringVariable(String value) implements Variable<String>
{
    @Override
    public Class<String> type()
    {
        return String.class;
    }
}
