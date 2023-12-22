package com.neep.neepmeat.fluid_util;

import net.minecraft.util.StringIdentifiable;

public enum PipeConnection implements StringIdentifiable
{
    SIDE("side"),
    SUPPORT("support"),
    NONE("none");

    private final String name;

    private PipeConnection(String name)
    {
        this.name = name;
    }

    @Override
    public String asString()
    {
        return this.name();
    }
}
