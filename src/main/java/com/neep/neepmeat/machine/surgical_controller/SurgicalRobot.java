package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SurgicalRobot implements NbtSerialisable
{
    private static final byte STATE_IDLE = 0;
    private static final byte STATE_LAUNCHING = 1;
    private static final byte STATE_ACTIVE = 2;
    private static final byte STATE_RETURNING = 3;
    private static final byte STATE_DOCKING = 4;
    private final PLCBlockEntity parent;
    private byte movementState;

    private byte nextType;  //0: base, 1: item, 2: fluid, 3: entity

    public static final double SPEED = 0.05;

    public double prevX;
    public double prevY;
    public double prevZ;
    private double x;
    private double y;
    private double z;
    public double clientX;
    public double clientY;
    public double clientZ;

    private double vx;
    private double vy;
    private double vz;

    @Nullable private Vec3d targetPos;
    @Nullable private BlockPos target;

    private final BlockPos basePos;
    private final Vec3d dockingPos;
    private final Vec3d attachPos;

    protected boolean pressingLeft;
    protected boolean pressingRight;
    protected boolean pressingForward;
    protected boolean pressingBack;
    protected boolean pressingUp;
    protected boolean pressingDown;
    protected boolean prevForward;
    protected boolean prevBack;
    protected boolean prevLeft;
    protected boolean prevRight;
    protected boolean prevUp;
    protected boolean prevDown;

    @Nullable private PlayerEntity controller;

    public SurgicalRobot(PLCBlockEntity parent)
    {
        this.parent = parent;
        this.basePos = parent.getPos();
        setTarget(basePos);
        this.dockingPos = Vec3d.ofCenter(basePos, 0.5);
        this.attachPos = Vec3d.ofCenter(basePos, 1.4);
        this.x = dockingPos.x;
        this.y = dockingPos.y;
        this.z = dockingPos.z;
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
    }

    public void setController(@Nullable PlayerEntity player)
    {
        this.controller = player;
    }

    @Nullable
    public PlayerEntity getController()
    {
        return controller;
    }

    public boolean shouldUpdatePosition(World world)
    {
        if (!world.isClient())
        {
            return getController() == null || parent.actionBlocksController();
        }
        else
        {
            return getController() != null;
        }
    }

    public void tick()
    {
        if (shouldUpdatePosition(parent.getWorld()))
        {
            move();
        }
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
            if (targetPos != null)
            {
                moveTo(targetPos);
            }
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
        if (toPos.squaredDistanceTo(x, y, z) >= 0.01 * 0.01)
        {
            double dx = (toPos.x - x);
            double dy = (toPos.y - y);
            double dz = (toPos.z - z);
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double vx = dx;
            double vy = dy;
            double vz = dz;

            // Normalise motion vector if distance is greater than base movement step
            if (dist > getSpeed())
            {
                vx = vx / dist * getSpeed();
                vy = vy / dist * getSpeed();
                vz = vz / dist * getSpeed();
            }

            // Step position
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
        return 0.1;
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
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putByte(NBT_MOVEMENT_STATE, movementState);
        nbt.putByte(NBT_NEXT_TYPE, nextType);
        nbt.putDouble("rx", x);
        nbt.putDouble("ry", y);
        nbt.putDouble("rz", z);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.movementState = nbt.getByte(NBT_MOVEMENT_STATE);
        this.nextType=nbt.getByte(NBT_NEXT_TYPE);
        this.x = nbt.getDouble("rx");
        this.y = nbt.getDouble("ry");
        this.z = nbt.getDouble("rz");
    }

//    public void writeBuf(PacketByteBuf buf)
//    {
//        buf.writeDouble(x);
//        buf.writeDouble(y);
//        buf.writeDouble(z);
//    }
//
//    public void readBuf(PacketByteBuf buf)
//    {
//        this.x = buf.readDouble();
//        this.y = buf.readDouble();
//        this.z = buf.readDouble();
//    }

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

    public void setPos(double x, double y, double z)
    {
        // Do not accept update packets from the client when overriding the controller
        if (!parent.getWorld().isClient() && shouldUpdatePosition(parent.getWorld()))
            return;

        this.x = x;
        this.y = y;
        this.z = z;
        parent.markDirty();
    }

    public void stay()
    {
        this.setTarget(new BlockPos(x, y, z));
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        private final SurgicalRobot robot;
        private final PLCBlockEntity be;

        public Client(SurgicalRobot robot, PLCBlockEntity be)
        {
            this.robot = robot;
            this.be = be;
        }

        public void tick()
        {
            updateKeys();
            motion();

            if (be.getWorld().getTime() % 4 == 0)
            {
                PLCRobotC2S.Client.send(be);
            }
        }

        public void motion()
        {
            if (!robot.parent.overrideController())
            {
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

                double speed = 0.2;
                float pitch = camera.getPitch();
                float yaw = camera.getYaw();
                double vx = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                double vz = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                Vec3d normal = new Vec3d(-vz, 0, vx);

                double fvx = 0;
                double fvy = 0;
                double fvz = 0;

                if (robot.pressingForward)
                {
                    fvx += vx;
                    fvz += vz;
                }
                if (robot.pressingBack)
                {
                    fvx -= vx;
                    fvz -= vz;
                }

                if (robot.pressingLeft)
                {
                    fvx -= normal.x;
                    fvz -= normal.z;
                }
                if (robot.pressingRight)
                {
                    fvx += normal.x;
                    fvz += normal.z;
                }

                if (robot.pressingUp)
                {
                    fvy += speed;
                }
                if (robot.pressingDown)
                {
                    fvy -= speed;
                }

                double l = Math.sqrt(fvx * fvx + fvz * fvz);
                if (l != 0)
                {
                    fvx = fvx / l * speed;
//                    fvy = fvy * speed;
                    fvz = fvz / l * speed;

                    robot.vz = fvx;
                    robot.vy = fvy;
                    robot.vx = fvz;
                }

                robot.x += fvx;
                robot.y += fvy;
                robot.z += fvz;

                robot.vz *= 0.4;
                robot.vy *= 0.4;
                robot.vx *= 0.4;
            }
        }

        public void updateKeys()
        {
            if (robot.controller == null)
                return;

            GameOptions options = MinecraftClient.getInstance().options;

            robot.prevForward = robot.pressingForward;
            robot.prevBack = robot.pressingBack;
            robot.prevLeft = robot.pressingLeft;
            robot.prevRight = robot.pressingRight;
            robot.prevUp = robot.pressingUp;
            robot.prevDown = robot.pressingDown;

            robot.pressingForward = options.forwardKey.isPressed();
            robot.pressingBack = options.backKey.isPressed();
            robot.pressingLeft = options.leftKey.isPressed();
            robot.pressingRight = options.rightKey.isPressed();
            robot.pressingUp = options.jumpKey.isPressed();
            robot.pressingDown = options.sneakKey.isPressed();
        }
    }
}
