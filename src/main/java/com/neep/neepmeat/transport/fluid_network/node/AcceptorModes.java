package com.neep.neepmeat.transport.fluid_network.node;

import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

public enum AcceptorModes
{

    NONE(0, 0),
    PULL(1, -1),
    PUSH(2, 1),
    INSERT_ONLY(3, 0),
    EXTRACT_ONLY(4, 0),
    INSERT_EXTRACT(5, 0),
    SENSOR(6, 0);

    private final float flow;
    private final int id;
    private static final AcceptorModes[] VALUES = Arrays.stream(values())
            .sorted(Comparator.comparingInt(mode -> mode.id))
            .toArray(AcceptorModes[]::new);

    public float getFlow()
    {
        return flow;
    }

    public int getId()
    {
        return id;
    }

    public boolean canInsert()
    {
        return this == INSERT_ONLY || this == INSERT_EXTRACT || this == PULL;
    }

    public boolean canFlow()
    {
        return this != NONE;
    }

    public boolean canExtract()
    {
        return this == EXTRACT_ONLY || this == INSERT_EXTRACT || this == PUSH;
    }

    public static AcceptorModes byId(int id)
    {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static AcceptorModes byFlow(float flow)
    {
        return flow > 0 ? PUSH : flow < 0 ? PULL : INSERT_EXTRACT;
    }

    AcceptorModes(int id, float pressure)
    {
        this.flow = pressure;
        this.id = id;
    }

    public boolean isDriving()
    {
        return this == PUSH || this == PULL;
    }
}
