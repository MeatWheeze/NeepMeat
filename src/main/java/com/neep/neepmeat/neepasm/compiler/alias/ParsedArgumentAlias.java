package com.neep.neepmeat.neepasm.compiler.alias;

import com.neep.neepmeat.plc.instruction.Argument;

public class ParsedArgumentAlias implements ParsedAlias
{
    private final String name;
    private final Argument argument;

    public ParsedArgumentAlias(String name, Argument argument)
    {
        this.name = name;
        this.argument = argument;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public Type type()
    {
        return Type.ARGUMENT;
    }

    public Argument argument()
    {
        return argument;
    }
}
