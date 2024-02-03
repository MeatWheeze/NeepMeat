package com.neep.neepmeat.neepasm.compiler.alias;

public interface ParsedAlias
{
    String name();

    Type type();

    enum Type
    {
        ARGUMENT
    }
}
