package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SurgicalRobot implements NbtSerialisable
{
    private static final byte STATE_IDLE = 0;
    private static final byte STATE_LAUNCHING = 1;
    private static final byte STATE_ACTIVE = 2;
    private static final byte STATE_RETURNING = 3;
    private static final byte STATE_DOCKING = 4;
    private byte movementState;

    private static final byte TYPE_BASE = 0;
    private static final byte TYPE_ITEM = 1;
    private static final byte TYPE_FLUID = 2;
    private static final byte TYPE_ENTITY = 3;
    private byte nextType;  //0: base, 1: item, 2: fluid, 3: entity

    public static final double SPEED = 0.05;

    private double x;
    private double y;
    private double z;

    public double clientX;
    public double clientY;
    public double clientZ;

    private Vec3d targetPos;

    private BlockPos target;
    private final BlockPos basePos;
    private final Vec3d dockingPos;
    private final Vec3d attachPos;

    public SurgicalRobot(BlockPos basePos)
    {
        this.basePos = basePos;
        setTarget(basePos);
        this.dockingPos = Vec3d.ofCenter(basePos, 0.5);
        this.attachPos = Vec3d.ofCenter(basePos, 1.4);
        this.x = dockingPos.x;
        this.y = dockingPos.y;
        this.z = dockingPos.z;
    }

    public void tick()
    {
        move();
    }

    protected void move()
    {
        if (movementState == STATE_IDLE)
        {
            if (!target.equals(basePos))
            {
                movementState = STATE_LAUNCHING;
            }
        }
        else if (movementState == STATE_LAUNCHING)
        {
            if (moveTo(attachPos)) movementState = STATE_ACTIVE;
        }
        else if (movementState == STATE_ACTIVE)
        {
            moveTo(targetPos);
        }
        else if (movementState == STATE_RETURNING)
        {
            if (moveTo(attachPos))
            {
                movementState = STATE_DOCKING;
//                setTarget(basePos);
            }
        }
        else if (movementState == STATE_DOCKING)
        {
            if (moveTo(dockingPos)) movementState = STATE_IDLE;
        }
    }

    private boolean moveTo(Vec3d toPos)
    {
        if (toPos.squaredDistanceTo(x, y, z) >= 0.1 * 0.1)
        {
            double dx = (toPos.x - x);
            double dy = (toPos.y - y);
            double dz = (toPos.z - z);
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double vx = dx / dist * getSpeed();
            double vy = dy / dist * getSpeed();
            double vz = dz / dist * getSpeed();
            x += vx;
            y += vy;
            z += vz;
            return false;
        }
        else return true;
    }

    public void setTarget(BlockPos target)
    {
        this.target = target.toImmutable();
        this.targetPos = getTarget(this.target);
        boolean bl = reachedTarget();
    }

    private Vec3d getTarget(BlockPos target)
    {
//        return switch (nextType)
//        {
//            case TYPE_ITEM -> Vec3d.ofCenter(target, 0.9);
//            case TYPE_FLUID -> Vec3d.ofCenter(target, 2.5);
//            default -> Vec3d.ofCenter(target, 1.5);
//        };
        return Vec3d.ofCenter(target);
}

    public double getSpeed()
    {
        return SPEED;
    }

    public Vec3d getPos()
    {
        return new Vec3d(x, y, z);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    private static final String NBT_MOVEMENT_STATE = "movementState";
    private static final String NBT_NEXT_TYPE = "nextType";

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        nbt.putByte(NBT_MOVEMENT_STATE, movementState);
        nbt.putByte(NBT_NEXT_TYPE, nextType);
        nbt.putDouble("x", x);
        nbt.putDouble("y", y);
        nbt.putDouble("z", z);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.movementState = nbt.getByte(NBT_MOVEMENT_STATE);
        this.nextType=nbt.getByte(NBT_NEXT_TYPE);
        this.x = nbt.getDouble("x");
        this.y = nbt.getDouble("y");
        this.z = nbt.getDouble("z");
    }

    public boolean isActive()
    {
        return movementState == STATE_ACTIVE;
    }

    public boolean reachedTarget()
    {
        return targetPos.squaredDistanceTo(x, y, z) <= 0.1 * 0.1;
    }

    public void returnToBase()
    {
        movementState = STATE_RETURNING;
        setTarget(basePos);
    }
}