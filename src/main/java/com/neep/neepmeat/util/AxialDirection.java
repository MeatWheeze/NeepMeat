package com.neep.neepmeat.util;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public enum AxialDirection implements StringIdentifiable
{
    POSITIVE(1, "positive"),
    NEGATIVE(-1, "negative");

    private final String id;
    private final int sign;

    AxialDirection(int sign, String id)
    {
        this.sign = sign;
        this.id = id;
    }

    @Override
    public String asString()
    {
        return id;
    }

    public int sign()
    {
        return sign;
    }

    public static AxialDirection from(Direction direction)
    {
        return direction.getAxis().choose(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()) > 0 ?
                POSITIVE : NEGATIVE;
    }

    public Vec3f with(Direction.Axis direction)
    {
        Vec3f vec = switch (direction)
        {
            case X -> Direction.EAST.getUnitVector();
            case Y -> Direction.UP.getUnitVector();
            case Z -> Direction.SOUTH.getUnitVector();
        };

        vec.multiplyComponentwise(sign, sign, sign);
        return vec;
    }
}
