package com.neep.neepmeat.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class PipeOffset
{
    public double x;
    public double y;
    public double z;

    public Direction in;
    public Direction out;
    public float progress;

    public PipeOffset(Direction in, Direction out)
    {
        this.in = in;
        this.out = out;
        this.progress = 0;
    }

    public static Vec3d directionUnit(Direction direction)
    {
//        Vec3d vec = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
//        return vec.subtract(vec.multiply(0.5));
        return new  Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public void set(Vec3d vec)
    {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void step(float delta)
    {
        progress = delta;
        float inFactor = (float) (1* (1 - delta));
        float outFactor = (float) (1 * delta);
        Vec3d vec;
        if (progress <= 0.5)
        {
            vec = directionUnit(in).multiply(inFactor - 0.5);
        }
        else
        {
            vec = directionUnit(out).multiply(outFactor - 0.5);
        }
        Vec3d newThis = vec;
        this.set(newThis);
    }
}
