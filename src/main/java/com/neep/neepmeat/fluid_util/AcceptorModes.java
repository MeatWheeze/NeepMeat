package com.neep.neepmeat.fluid_util;

public enum AcceptorModes
{

    NONE(0),
    PULL(-1),
    PUSH(1),
    INSERT_ONLY(0),
    EXTRACT_ONLY(0),
    INSERT_EXTRACT(0);

    private final float flow;

    public float getFlow()
    {
        return flow;
    }

    AcceptorModes(float pressure)
    {
        this.flow = pressure;
    }
}
