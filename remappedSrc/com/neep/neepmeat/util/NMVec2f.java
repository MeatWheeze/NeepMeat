package com.neep.neepmeat.util;

public class NMVec2f extends net.minecraft.util.math.Vec2f
{
    public NMVec2f(float x, float y)
    {
        super(x, y);
    }

    @Override
    public String toString()
    {
        return "x: " + x + ", y: " + y;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public boolean isWithin(float x1, float y1, float difference)
    {
        return NMMaths.isWithin(x, x1, difference) && NMMaths.isWithin(y, y1, difference);
    }
}
