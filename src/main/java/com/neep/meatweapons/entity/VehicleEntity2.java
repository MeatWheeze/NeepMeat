/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package com.neep.meatweapons.entity;

import com.google.common.collect.Lists;
import com.neep.meatweapons.client.MWKeys;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VehicleEntity2
extends Entity {
    private float velocityDecay;
    private float yawVelocity;
    private int delta;
    private double x;
    private double y;
    private double z;
    private double boatYaw;
    private double boatPitch;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingUp;
    private boolean pressingDown;
    private double fallVelocity;
    protected boolean powered = true;

    public VehicleEntity2(EntityType<? extends Entity> entityType, World world)
    {
        super(entityType, world);
        this.inanimate = true;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
    {
        return dimensions.height;
    }

    @Override
    protected MoveEffect getMoveEffect()
    {
        return MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker()
    {
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return VehicleEntity2.canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other)
    {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect)
    {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }

    @Override
    public double getMountedHeightOffset() {
        return -0.1;
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        if (this.world.isClient || this.isRemoved())
        {
            return true;
        }
        this.scheduleVelocityUpdate();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            this.dropItem(this.asItem());
        }
        this.discard();
        return true;
    }

    private ItemConvertible asItem()
    {
        return Items.DIRT.asItem();
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof VehicleEntity2) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public void animateDamage()
    {
        getEntityWorld().addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, 0, 0, 0);
    }

    @Override
    public boolean collides()
    {
        return !this.isRemoved();
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.boatYaw = yaw;
        this.boatPitch = pitch;
        this.delta = 10;
    }

    @Override
    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public void tick()
    {
        super.tick();
        this.interpolatePosition();
        if (this.isLogicalSideForUpdatingMovement())
        {
            this.updateVelocity();
            if (this.world.isClient)
            {
                updateKeys();
                this.updateMotion();
            }
            this.move(MovementType.SELF, this.getVelocity());
        }
        else
        {
            this.setVelocity(Vec3d.ZERO);
        }

        this.checkBlockCollision();

        List<Entity> otherEntities = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
        if (!otherEntities.isEmpty()) {
            boolean soundEvent = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (Entity entity : otherEntities)
            {
                if (entity.hasPassenger(this))
                    continue;
                if (soundEvent && this.getPassengerList().size() < 2 && !entity.hasVehicle() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity))
                {
                    entity.startRiding(this);
                    continue;
                }
                this.pushAwayFrom(entity);
            }
        }
    }

    private void interpolatePosition()
    {
        if (this.isLogicalSideForUpdatingMovement())
        {
            this.delta = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.delta <= 0)
        {
            return;
        }
        double d = this.getX() + (this.x - this.getX()) / (double) this.delta;
        double e = this.getY() + (this.y - this.getY()) / (double) this.delta;
        double f = this.getZ() + (this.z - this.getZ()) / (double) this.delta;
        double g = MathHelper.wrapDegrees(this.boatYaw - (double) this.getYaw());
        this.setYaw(this.getYaw() + (float)g / (float)this.delta);
        this.setPitch(this.getPitch() + (float) (this.boatPitch - (double) this.getPitch()) / (float) this.delta);
        --this.delta;
        this.setPosition(d, e, f);
        this.setRotation(this.getYaw(), this.getPitch());
    }

    private void updateVelocity()
    {
        this.velocityDecay = 0.9f;
        float verticalVelocityDecay = 0.1f;

        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)this.velocityDecay, vec3d.y * verticalVelocityDecay, vec3d.z * (double)this.velocityDecay);
        this.yawVelocity *= this.velocityDecay;
    }

    protected void updateKeys()
    {
        GameOptions options = MinecraftClient.getInstance().options;
        this.pressingForward = options.keyForward.isPressed();
        this.pressingBack = options.keyBack.isPressed();
        this.pressingLeft = options.keyLeft.isPressed();
        this.pressingRight = options.keyRight.isPressed();
        this.pressingUp = options.keyJump.isPressed();
        this.pressingDown = MWKeys.AIRTRUCK_DOWN.isPressed();
    }

    private void updateMotion()
    {
        if (!this.hasPassengers())
        {
            return;
        }

        float forwardsVelocity = 0.0f;
        float upVelocity = 0.0f;
        if (this.pressingLeft)
        {
            this.yawVelocity -= 1.0f;
        }
        if (this.pressingRight)
        {
            this.yawVelocity += 1.0f;
        }
        if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack)
        {
            forwardsVelocity += 0.005f;
        }
        this.setYaw(this.getYaw() + this.yawVelocity);
        if (this.pressingForward)
        {
            forwardsVelocity += 0.04f;
        }
        if (this.pressingBack)
        {
            forwardsVelocity -= 0.005f;
        }
        if (this.pressingUp)
        {
            upVelocity += 0.08;
        }
        if (this.pressingDown)
        {
            upVelocity -= 0.08;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity,
                upVelocity,
                MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity));
    }

    @Override
    public void updatePassengerPosition(Entity passenger)
    {
        if (!this.hasPassenger(passenger))
        {
            return;
        }

        float driverOffset = 1.0f;
        float trailerOffset = -1f;
        float xOffset = driverOffset;
        float yOffset = (float) (this.getMountedHeightOffset() + passenger.getHeightOffset());

        if (this.getPassengerList().size() > 1)
        {
            int i = this.getPassengerList().indexOf(passenger);
            xOffset = i == 0 ? driverOffset : trailerOffset;
            if (passenger instanceof AnimalEntity)
            {
                xOffset = xOffset + 0.2f;
            }
        }

        Vec3d i = new Vec3d(xOffset, 0.0, 0.0).rotateY((float) (-this.getYaw() * Math.PI / 180 - Math.PI / 2));

        passenger.setPosition(this.getX() + i.x, this.getY() + yOffset, this.getZ() + i.z);
        passenger.setYaw(passenger.getYaw() + this.yawVelocity);
        passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);

        this.copyEntityData(passenger);

        if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1)
        {
            int animalYaw = passenger.getId() % 2 == 0 ? 90 : 270;
            passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + animalYaw);
            passenger.setHeadYaw(passenger.getHeadYaw() + animalYaw);
        }
    }

    protected void copyEntityData(Entity entity)
    {
        entity.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
        float g = MathHelper.clamp(f, -105.0f, 105.0f);
        entity.prevYaw += g - f;
        entity.setYaw(entity.getYaw() + g - f);
        entity.setHeadYaw(entity.getYaw());
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        double e;
        Vec3d vec3d = VehicleEntity2.getPassengerDismountOffset(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO, passenger.getWidth(), passenger.getYaw());
        double d = this.getX() + vec3d.x;
        BlockPos blockPos = new BlockPos(d, this.getBoundingBox().maxY, e = this.getZ() + vec3d.z);
        BlockPos blockPos2 = blockPos.down();
        if (!this.world.isWater(blockPos2)) {
            double g;
            ArrayList<Vec3d> list = Lists.newArrayList();
            double f = this.world.getDismountHeight(blockPos);
            if (Dismounting.canDismountInBlock(f)) {
                list.add(new Vec3d(d, (double)blockPos.getY() + f, e));
            }
            if (Dismounting.canDismountInBlock(g = this.world.getDismountHeight(blockPos2))) {
                list.add(new Vec3d(d, (double)blockPos2.getY() + g, e));
            }
            for (EntityPose entityPose : passenger.getPoses()) {
                for (Vec3d vec3d2 : list) {
                    if (!Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose)) continue;
                    passenger.setPose(entityPose);
                    return vec3d2;
                }
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public void onPassengerLookAround(Entity passenger)
    {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        if (player.shouldCancelInteraction())
        {
            return ActionResult.PASS;
        }
        if (!this.world.isClient)
        {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (this.hasVehicle())
        {
            return;
        }
        if (onGround)
        {
            if (this.fallDistance > 3.0f && !powered)
            {
                this.handleFallDamage(this.fallDistance, 1.0f, DamageSource.FALL);
                if (!this.world.isClient && !this.isRemoved())
                {
                    this.kill();
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                    {
                    }
                }
            }
            this.fallDistance = 0.0f;
        }
        else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0)
        {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return this.getPassengerList().size() < 2;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public ItemStack getPickBlockStack()
    {
        return Items.DIRT.getDefaultStack();
    }
}

