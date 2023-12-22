package com.neep.neepmeat.maths;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.Direction.*;

public class NMMaths
{
    public static boolean isWithin(float value, float target, float difference)
    {
        return Math.abs(target - value) < difference;
    }

    public static NMVec2f removeAxis(Direction.Axis axis, Vec3d vec)
    {
        NMVec2f out;
        switch (axis)
        {
            case X -> out = new NMVec2f((float) vec.getY(), (float) vec.getZ());
            case Y -> out = new NMVec2f((float) vec.getX(), (float) vec.getZ());
            case Z -> out = new NMVec2f((float) vec.getX(), (float) vec.getY());
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }
        return out;
    }

    // An ugly bodge for a very specific situation.
    public static Direction swapDirections(Direction direction)
    {
        switch (direction)
        {
            case UP:
            {
                return EAST;
            }
            case DOWN:
            {
                return WEST;
            }
            case EAST:
            {
                return DOWN;
            }
            case WEST:
            {
                return UP;
            }
        }
        throw new IllegalStateException("Unable to get X-rotated facing of " + direction);
    }
}
