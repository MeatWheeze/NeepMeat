package com.neep.neepmeat.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
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
            case UP -> {
                return EAST;
            }
            case DOWN -> {
                return WEST;
            }
            case EAST -> {
                return DOWN;
            }
            case WEST -> {
                return UP;
            }
        }
        throw new IllegalStateException("Unable to get X-rotated facing of " + direction);
    }

    public static double manhattanDistance(Vec3d v1, Vec3d v2)
    {
        double d = Math.abs(v2.x - v1.x);
        double e = Math.abs(v2.y - v1.y);
        double f = Math.abs(v2.z - v1.z);
        return d + e + f;
    }

    public static Vec2f flatten(Vec3d vec)
    {
        return new Vec2f((float) vec.getX(), (float) vec.getZ());
    }

    public static float getAngle(Vec3d v1, Vec3d v2)
    {
        return (float) Math.acos(v1.dotProduct(v2) / (v1.length() * v2.length()));
    }

    public static float getAngle(Vec2f v1)
    {
        return (float) ((float) Math.atan2(v1.x, v1.y) + Math.PI);
    }

    public static float angleLerp(float delta, float x1, float x2)
    {
        // StackOverflow magic
        var CS = (1 - delta) * Math.cos(x1) + delta * Math.cos(x2);
        var SN = (1 - delta) * Math.sin(x1) + delta * Math.sin(x2);
        var C = Math.atan2(SN, CS);
        return (float) C;
    }

}
