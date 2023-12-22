package com.neep.neepmeat.fluid_util;

import net.minecraft.util.StringIdentifiable;

public enum PipeConnection implements StringIdentifiable
{
    SIDE("true"),
    FORCED("forced"),
    NONE("false");

    private final String name;

    PipeConnection(String name)
    {
        this.name = name;
    }

    @Override
    public String asString()
    {
        return this.name;
    }

    public boolean isConnected()
    {
        return this != NONE;
    }
}
