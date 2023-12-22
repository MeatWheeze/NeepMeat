package com.neep.neepmeat.fluid_transfer;

import net.minecraft.util.StringIdentifiable;

public enum PipeConnectionType implements StringIdentifiable
{
    SIDE("true"),
    FORCED("forced"),
    NONE("false");

    private final String name;

    PipeConnectionType(String name)
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
        return this == SIDE;
    }

    public boolean canBeChanged()
    {
        return this == SIDE || this == NONE;
    }
}
