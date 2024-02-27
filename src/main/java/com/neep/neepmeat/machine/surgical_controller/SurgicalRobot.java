package com.neep.neepmeat.machine.surgical_controller;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.robot.PLCRobot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.neep.neepmeat.machine.surgical_controller.SurgicalRobot.MovementState.STATE_ACTIVE;
import static com.neep.neepmeat.machine.surgical_controller.SurgicalRobot.MovementState.STATE_DOCKING;

public class SurgicalRobot implements PLCRobot, NbtSerialisable
{
    private final PLCBlockEntity parent;
    private MovementState movementState = MovementState.STATE_IDLE;

    public double cameraX;
    public double cameraY;
    public double cameraZ;
    private double x;
    private double y;
    private double z;
    public double clientX;
    public double clientY;
    public double clientZ;
    public float clientYaw;

    private double vx;
    private double vy;
    private double vz;

    public double prevX;
    public double prevY;
    public double prevZ;

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

    private float pitch;
    private float yaw;

    // I can't be bothered to do this properly
    public ResourceAmount<ItemVariant> stored;
    private boolean moved = true;

    public SurgicalRobot(PLCBlockEntity parent)
    {
        this.parent = parent;
        this.basePos = parent.getPos();
        this.dockingPos = Vec3d.ofCenter(basePos, 0.5);
        this.attachPos = Vec3d.ofCenter(basePos, 1.4);
        this.x = dockingPos.x;
        this.y = dockingPos.y;
        this.z = dockingPos.z;
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;

        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
    }

    public void setController(@Nullable PlayerEntity player)
    {
        this.controller = player;
//        if (controller == null && shouldUpdatePosition(parent.getWorld()))
//        {
//            setTarget(new BlockPos(x, y, z));
//        }
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
            return (getController() == null || parent.actionBlocksController());
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
        if (movementState == MovementState.STATE_IDLE)
        {
            if (!Objects.equals(target, basePos))
            {
                movementState = MovementState.STATE_LAUNCHING;
            }
        }
        else if (movementState == MovementState.STATE_LAUNCHING)
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
        else if (movementState == MovementState.STATE_RETURNING)
        {
            if (moveTo(attachPos))
            {
                movementState = STATE_DOCKING;
            }
        }
        else if (movementState == STATE_DOCKING)
        {
            if (moveTo(dockingPos)) movementState = MovementState.STATE_IDLE;
        }
    }

    private boolean moveTo(Vec3d toPos)
    {
        if (toPos.squaredDistanceTo(x, y, z) >= 0.01 * 0.01)
        {
            double dx = (toPos.x - x);
            double dy = (toPos.y - y);
            double dz = (toPos.z - z);
            this.yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
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
            moved = true;
            return false;
        }
        else return true;
    }

    @Override
    public void setTarget(@Nullable PLC plc, @Nullable BlockPos target)
    {
        if (target == null)
        {
            this.target = null;
            this.targetPos = null;
            return;
        }

        this.target = target.toImmutable();
        this.targetPos = getTarget(this.target);
    }

    private Vec3d getTarget(BlockPos target)
    {
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

    public BlockPos getBlockPos()
    {
        return BlockPos.ofFloored(x, y, z);
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

    @Override
    public boolean actuatorRemoved()
    {
        return parent.isRemoved();
    }

    private static final String NBT_MOVEMENT_STATE = "movementState";
    private static final String NBT_NEXT_TYPE = "nextType";

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt(NBT_MOVEMENT_STATE, movementState.ordinal());
        nbt.putDouble("rx", x);
        nbt.putDouble("ry", y);
        nbt.putDouble("rz", z);

        nbt.putFloat("pitch", pitch);
        nbt.putFloat("yaw", yaw);

        if (stored != null)
        {
            NbtCompound storedNbt = new NbtCompound();
            storedNbt.put("item", stored.resource().toNbt());
            storedNbt.putLong("amount", stored.amount());
            nbt.put("stored", storedNbt);
        }

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.movementState = MovementState.values()[nbt.getInt(NBT_MOVEMENT_STATE)];
        this.x = nbt.getDouble("rx");
        this.y = nbt.getDouble("ry");
        this.z = nbt.getDouble("rz");

        this.pitch = nbt.getFloat("pitch");
        this.yaw = nbt.getFloat("yaw");
        this.clientYaw = yaw;

        if (nbt.contains("stored"))
        {
            NbtCompound storedNbt = nbt.getCompound("stored");
            stored = new ResourceAmount<>(
                    ItemVariant.fromNbt(storedNbt.getCompound("item")),
                    storedNbt.getLong("amount")
            );
        }
    }

    public boolean isActive()
    {
        return movementState == STATE_ACTIVE;
    }

    @Override
    public boolean reachedTarget(PLC plc)
    {
        return targetPos == null || targetPos.squaredDistanceTo(x, y, z) <= 0.1 * 0.1;
    }

    public void returnToBase()
    {
        movementState = MovementState.STATE_RETURNING;
        setTarget(null, basePos);
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
        this.setTarget(null, null);
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void setPitchYaw(float pitch, float yaw)
    {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void spawnItem(@Nullable ResourceAmount<ItemVariant> stored)
    {
        if (stored == null)
            return;
        ItemScatterer.spawn(parent.getWorld(), x, y, z, stored.resource().toStack((int) stored.amount()));
    }

    public void dumpStored(PLC plc)
    {
        if (stored != null)
        {
            spawnItem(stored);
            stored = null;
        }
    }

    @Override
    public void setStored(PLC plc, @Nullable ResourceAmount<ItemVariant> stored)
    {
        this.stored = stored;
    }

    @Override
    public BlockPos getBasePos()
    {
        return parent.getPos();
    }

    @Override
    public @Nullable ResourceAmount<ItemVariant> getStored(PLC plc)
    {
        return stored;
    }

    public void syncPosition(ServerWorld serverWorld)
    {
        if (!moved)
            return;

        PLCRobotC2S.send(parent, serverWorld);
        moved = false;
    }

    public enum MovementState
    {
        STATE_IDLE,
        STATE_LAUNCHING,
        STATE_ACTIVE,
        STATE_RETURNING,
        STATE_DOCKING
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

                    robot.vx = fvx;
                    robot.vz = fvz;
                }

                if (fvy != 0)
                {
                    robot.vy = fvy;
                }

                robot.x += robot.vx;
                robot.y += robot.vy;
                robot.z += robot.vz;

                robot.vz *= 0.05;
                robot.vy *= 0.05;
                robot.vx *= 0.05;
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
