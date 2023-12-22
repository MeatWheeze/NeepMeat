package com.neep.meatweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVehicleEntity extends VehicleEntity2
{
//    private double x;
//    private double y;
//    private double z;
//    public float vehicleYaw;
//    public float vehiclePitch;
//    private boolean pressingLeft;
//    private boolean pressingRight;
//    private boolean pressingForward;
//    private boolean pressingBack;
//    private double velocityDecay;
//    private float yawVelocity;
//    private int field_7708;
//
//    protected boolean accelForwards;
//    protected boolean accelBackwards;
//    protected boolean accelLeft;
//    protected boolean accelRight;
//    private double forwardVelocity;

    public double maxSpeed = 0.1;

    public AbstractVehicleEntity(EntityType<? extends Entity> type, World world)
    {
        super(type, world);
        this.inanimate = true;
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public Direction getMovementDirection()
    {
        return this.getHorizontalFacing().rotateYClockwise();
    }

    protected MoveEffect getMoveEffect()
    {
        return MoveEffect.NONE;
    }

    public void tick()
    {
        super.tick();
//        fixPosition();
//        if (this.isLogicalSideForUpdatingMovement())
//        {
//            this.updateVelocity();
//            if (this.world.isClient)
//            {
////                updateInputs(
////                        pressingForward,
////                        pressingBack,
////                        pressingLeft,
////                        pressingRight);
//            }
//            this.move(MovementType.SELF, this.getVelocity());
//        } else {
//            this.setVelocity(Vec3d.ZERO);
//        }
//        checkBlockCollision();
    }

    public double accel = 0.2;

//    protected void updateInputs(boolean forwards, boolean backwards, boolean left, boolean right)
//    {
////        double forwardVelocity = 0;
////        Vec3d unit = new Vec3d(- Math.sin(getYaw() * Math.PI / 180) * Math.cos(pitch * Math.PI / 180),
////                0, //Math.sin(pitch)
////                - Math.cos(getYaw() * Math.PI / 180) * Math.cos(getPitch() * Math.PI / 180));
////        if (forwards)
////        {
////            forwardVelocity = -0.2;
////        }
////        else if (backwards)
////        {
////            forwardVelocity = 0.2;
////        }
////        else
////        {
////            setVelocity(getVelocity().multiply(0));
////        }
////        setVelocity(unit.multiply(forwardVelocity));
////    }
//
//        if (!this.hasPassengers())
//        {
//            return;
//        }
//
//        maxSpeed = 0.1;
//        if (this.pressingForward && !this.pressingBack)
//        {
//            this.forwardVelocity = Math.min(this.forwardVelocity + 0.01f, maxSpeed);
////            this.forwardVelocity += 0.04f;
////            this.forwardVelocity = MathHelper.clamp(forwardVelocity, 0f, 0.7f);
//        }
//        else if (this.pressingBack && !this.pressingForward)
//        {
//            this.forwardVelocity = Math.max(this.forwardVelocity - 0.01f, - maxSpeed);
////            this.forwardVelocity -= 0.04f;
////            this.forwardVelocity = MathHelper.clamp(forwardVelocity, -0.7f, 0);
//        }
//        else
//        {
//            this.forwardVelocity *= 0.5;
//        }
//
//        if (this.pressingLeft && !this.pressingRight)
//        {
////            this.yawVelocity = (float) Math.min(this.yawVelocity + 0.1f, maxSpeed);
//        }
//        else if (this.pressingRight && !this.pressingLeft)
//        {
////            this.yawVelocity = (float) Math.max(this.yawVelocity - 0.1f, - maxSpeed);
//        }
//        else
//        {
//            this.yawVelocity *= 0.5;
//        }
//        this.setYaw(this.getYaw() + this.yawVelocity);
//
////        if (this.pressingLeft)
////        {
////            this.yawVelocity -= 1.0f;
////        }
////        if (this.pressingRight)
////        {
////            this.yawVelocity += 1.0f;
////        }
////        if (this.pressingForward)
////        {
////            forwardVelocity += 0.04f;
////        }
////        if (this.pressingBack)
////        {
////            forwardVelocity -= 0.005f;
////        }
//        this.setVelocity(this.getVelocity().add(
//                MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * forwardVelocity,
//                0.0,
//                MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * forwardVelocity));
//    }
//
//    private void updateVelocity()
//    {
//        double d = -0.04f;
//        double f = 0.0;
//        Vec3d vec3d = this.getVelocity();
//        this.velocityDecay = 0.9;
//        this.setVelocity(vec3d.x * (double) this.velocityDecay, vec3d.y, vec3d.z * (double) this.velocityDecay);
//        this.yawVelocity *= this.velocityDecay;
//        if (f > 0.0) {
//            Vec3d vec3d2 = this.getVelocity();
//            this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75, vec3d2.z);
//        }
//    }
//
//    private void fixPosition()
//    {
//        if (this.isLogicalSideForUpdatingMovement())
//        {
//            this.field_7708 = 0;
//            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
////            this.setPosition(this.getX(), this.getY(), this.getZ());
//        }
//        if (this.field_7708 <= 0)
//        {
//            return;
//        }
//        double d = this.getX() + (this.x - this.getX()) / (double) this.field_7708;
//        double e = this.getY() + (this.y - this.getY()) / (double) this.field_7708;
//        double f = this.getZ() + (this.z - this.getZ()) / (double) this.field_7708;
//        double g = MathHelper.wrapDegrees(this.vehicleYaw - (double) this.getYaw());
//        this.setYaw(this.getYaw() + (float) g / (float) this.field_7708);
//        this.setPitch(this.getPitch() + (float)(this.vehiclePitch - (double) this.getPitch()) / (float) this.field_7708);
//        --this.field_7708;
//        this.setPosition(d, e, f);
//        this.setRotation(this.getYaw(), this.getPitch());
//    }
//
//    @Override
//    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.vehicleYaw = yaw;
//        this.vehiclePitch = pitch;
//        this.field_7708 = 10;
//    }
//
//    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack)
//    {
//        this.pressingLeft = pressingLeft;
//        this.pressingRight = pressingRight;
//        this.pressingForward = pressingForward;
//        this.pressingBack = pressingBack;
//    }

    public boolean isLogicalSideForUpdatingMovement()
    {
        return super.isLogicalSideForUpdatingMovement();
    }

    public ActionResult interact(PlayerEntity player, Hand hand)
    {

        if (!this.world.isClient)
        {
            player.setYaw(this.getYaw());
            player.setPitch(this.getPitch());
            player.startRiding(this);
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    public Entity getPrimaryPassenger()
    {
        return getFirstPassenger();
    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public boolean collides()
    {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable()
    {
        return true;
    }

    @Override
    public boolean isPushable()
    {
        return true;
    }

}
