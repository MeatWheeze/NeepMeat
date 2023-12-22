package com.neep.neepmeat.util;

import net.minecraft.util.StringIdentifiable;

public enum LinearDirection implements StringIdentifiable
{
    FORWARDS("forwards"),
    BACKWARDS("backwards"),
    STOP("stop");

    private final String name;

    LinearDirection(String name)
    {
        this.name = name;
    }

    @Override
    public String asString()
    {
        return this.name;
    }
}
