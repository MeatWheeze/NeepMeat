package com.neep.neepmeat.entity;

import com.neep.meatweapons.damage.MWDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;

public abstract class SimpleEntity extends Entity
{
    private static final TrackedData<Float> SIDEWAYS_SPEED = DataTracker.registerData(SimpleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FORWARD_SPEED = DataTracker.registerData(SimpleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> UPWARD_SPEED = DataTracker.registerData(SimpleEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected int trackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverPitch;
    protected double serverYaw;

    private float sidewaysSpeed;
    private float forwardSpeed;
    private float upwardSpeed;

    public SimpleEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void initDataTracker()
    {
        this.dataTracker.startTracking(SIDEWAYS_SPEED, 0f);
        this.dataTracker.startTracking(FORWARD_SPEED, 0f);
        this.dataTracker.startTracking(UPWARD_SPEED, 0f);
    }

    protected float getBaseMovementSpeedMultiplier()
    {
        return 0.8f;
    }

    public boolean shouldSwim()
    {
        return false;
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate)
    {
        this.serverX = x;
        this.serverY = y;
        this.serverZ = z;
        this.serverYaw = yaw;
        this.serverPitch = pitch;
        this.trackingIncrements = interpolationSteps;
    }

    protected void interpolatePosition()
    {
        if (this.trackingIncrements > 0)
        {
            double d = this.getX() + (this.serverX - this.getX()) / (double) this.trackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double) this.trackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double) this.trackingIncrements;
            double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
            this.setYaw(this.getYaw() + (float) g / (float) this.trackingIncrements);
            this.setPitch(this.getPitch() + (float)(this.serverPitch - (double) this.getPitch()) / (float) this.trackingIncrements);

            --this.trackingIncrements;

            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }

    public void tickMovement()
    {
        if (this.isLogicalSideForUpdatingMovement())
        {
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        interpolatePosition();

        Vec3d vec3d = this.getVelocity();
        double h = vec3d.x;
        double i = vec3d.y;
        double j = vec3d.z;

        // Cut off at low speeds
        if (Math.abs(vec3d.x) < 0.003) h = 0.0;
        if (Math.abs(vec3d.y) < 0.003) i = 0.0;
        if (Math.abs(vec3d.z) < 0.003) j = 0.0;

        this.setVelocity(h, i, j);

        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98f;
        this.forwardSpeed *= 0.98f;
        this.travel(new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed));
        this.world.getProfiler().pop();

        this.world.getProfiler().push("push");
        tickCramming();
        this.world.getProfiler().pop();
    }

    public Vec3d applyVerticalSpeed(double d, boolean bl, Vec3d vec3d)
    {
        if (!this.hasNoGravity() && !this.isSprinting())
        {
            double e = bl && Math.abs(vec3d.y - 0.005) >= 0.003 && Math.abs(vec3d.y - d / 16.0) < 0.003 ? -0.003 : vec3d.y - d / 16.0;
            return new Vec3d(vec3d.x, e, vec3d.z);
        }
        return vec3d;
    }

    protected void swimInWater(Vec3d movementInput)
    {
        double y = this.getY();
        float forward = this.isSprinting() ? 0.9f : this.getBaseMovementSpeedMultiplier();
        float g = 0.02f;
        this.updateVelocity(g, movementInput);
        this.move(MovementType.SELF, this.getVelocity());
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.multiply(forward, 0.8f, forward));
        Vec3d vec3d2 = this.applyVerticalSpeed(0.08f, false, this.getVelocity());
        this.setVelocity(vec3d2);
        if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + (double)0.6f - this.getY() + y, vec3d2.z))
        {
            this.setVelocity(vec3d2.x, 0.3f, vec3d2.z);
        }
    }

    protected void swimInLava(Vec3d movementInput, double d)
    {
        Vec3d vec3d3;
        double e = this.getY();
        this.updateVelocity(0.02f, movementInput);
        this.move(MovementType.SELF, this.getVelocity());
        if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight())
        {
            this.setVelocity(this.getVelocity().multiply(0.5, 0.8f, 0.5));
            vec3d3 = this.applyVerticalSpeed(d, false, this.getVelocity());
            this.setVelocity(vec3d3);
        }
        else
        {
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
        if (!this.hasNoGravity())
        {
            this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
        }
        vec3d3 = this.getVelocity();
        if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + (double)0.6f - this.getY() + e, vec3d3.z))
        {
            this.setVelocity(vec3d3.x, 0.3f, vec3d3.z);
        }
    }

    protected void moveOnLand(Vec3d movementInput, double d)
    {
        BlockPos blockPos = this.getVelocityAffectingPos();
        float slipperiness = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
        float f = this.onGround ? slipperiness * 0.91f : 0.91f;
        Vec3d vec3d6 = this.applyMovementInput(movementInput, slipperiness);
        double q = vec3d6.y;
        if (!this.world.isClient || this.world.isChunkLoaded(blockPos))
        {
            if (!this.hasNoGravity())
            {
                q -= d;
            }
        }
        else
        {
            q = this.getY() > (double)this.world.getBottomY() ? -0.1 : 0.0;
        }
        this.setVelocity(vec3d6.x * (double)f, q * (double)0.98f, vec3d6.z * (double)f);
    }

    public void travel(Vec3d movementInput)
    {
        if (this.isLogicalSideForUpdatingMovement())
        {
            double d = 0.08;
            if (this.isTouchingWater() && this.shouldSwim())
            {
                swimInWater(movementInput);
            }
            else if (this.isInLava() && shouldSwim())
            {
                swimInLava(movementInput, d);
            }
            else
            {
                moveOnLand(movementInput, d);
            }
        }
    }

    public Vec3d applyMovementInput(Vec3d movementInput, float slipperiness)
    {
        this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
//        this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
        this.move(MovementType.SELF, this.getVelocity());
        //        if ((this.horizontalCollision) && (g() || this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this))) {
//            vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
//        }
        return this.getVelocity();
    }

    protected float getMovementSpeed()
    {
        return 0.1f;
    }

    private float getMovementSpeed(float slipperiness)
    {
        return this.getMovementSpeed() * (0.2f / (slipperiness * slipperiness * slipperiness));
    }

    protected void tickCramming()
    {
        List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty())
        {
            int j;
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0)
            {
                j = 0;
                for (int k = 0; k < list.size(); k++)
                {
                    Entity entity = list.get(k);
                    if (entity.hasVehicle()) continue;
                    ++j;
                }
                if (j > i - 1)
                {
                    this.damage(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (j = 0; j < list.size(); ++j)
            {
                Entity entity = list.get(j);
                this.pushAwayFrom(entity);
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {

    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public boolean canHit()
    {
        return true;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return other.isCollidable();
    }

    @Override
    public boolean isPushable()
    {
        return true;
    }
}
