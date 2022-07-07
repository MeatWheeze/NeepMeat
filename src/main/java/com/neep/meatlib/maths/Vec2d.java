package com.neep.meatlib.maths;

public class Vec2d
{
    public double x;
    public double y;

    public Vec2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Vec2d other)
    {
        return distanceTo(this, other);
    }

    public static double distanceTo(double x1, double y1, double x2, double y2)
    {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distanceTo(Vec2d v1, Vec2d v2)
    {
        return distanceTo(v1.x, v1.y, v2.x, v2.y);
    }
}
