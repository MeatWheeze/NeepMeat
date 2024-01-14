package com.neep.neepmeat.machine.phage_ray;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhageRayEntity extends Entity
{
    @Nullable private PhageRayBlockEntity parent;

    public PhageRayEntity(EntityType<?> type, World world)
    {
        super(type, world);
        intersectionChecked = true;
    }

    @Override
    protected void initDataTracker()
    {

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
    public void tick()
    {
        super.tick();
        if (!world.isClient() && (age > 5 && (parent == null || parent.isRemoved())))
        {
            remove(RemovalReason.DISCARDED);
        }

        if (!world.isClient())
        {
            if (hasPassengers() && getFirstPassenger() != null)
            {
                this.setYaw(getFirstPassenger().getYaw());
                this.setPitch(limitPitch(getFirstPassenger().getPitch()));

                if (world.getTime() % 2 == 0)
                {
                    RaycastContext context = new RaycastContext(
                            getPos().add(0, 1.5, 0),
                            getPos().add(getRotationVector().multiply(30)),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            this);

                    BlockHitResult result = world.raycast(context);
                    if (result.getType() == HitResult.Type.BLOCK)
                    {
                        world.breakBlock(result.getBlockPos(), false);
                    }
                }
            }
        }
    }

    private float limitPitch(float pitch)
    {
        return MathHelper.clamp(pitch, -90, 30);
    }

    public boolean canHit()
    {
        return !this.isRemoved();
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger)
    {
        return getPos().add(0, getHeight(), 0);
//        Vec3d vec3d = AbstractVehicleEntity.getPassengerDismountOffset(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO, passenger.getWidth(), passenger.getYaw());
//        double posX = this.getX() + vec3d.x;
//        double posZ = this.getZ() + vec3d.z;
//        BlockPos blockPos = new BlockPos(posX, this.getBoundingBox().maxY, posZ).down();
//        if (!this.world.isWater(blockPos))
//        {
//            ArrayList<Vec3d> list = Lists.newArrayList();
//            double f = this.world.getDismountHeight(blockPos);
//            if (Dismounting.canDismountInBlock(f))
//            {
//                list.add(new Vec3d(posX, blockPos.getY() + f, posZ));
//            }
//            double g = this.world.getDismountHeight(blockPos);
//            if (Dismounting.canDismountInBlock(g))
//            {
//                list.add(new Vec3d(posX, blockPos.getY() + g, posZ));
//            }
//            for (EntityPose entityPose : passenger.getPoses())
//            {
//                for (Vec3d vec3d2 : list)
//                {
//                    if (!Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose))
//                        continue;
//                    passenger.setPose(entityPose);
//                    return vec3d2;
//                }
//            }
//        }
//        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public boolean canUsePortals()
    {
        return false;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement)
    {
        super.move(movementType, movement);
    }

    @Override
    public boolean collidesWith(Entity other)
    {
        return super.collidesWith(other);
    }

    @Override
    public boolean isCollidable()
    {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        if (hasPassengers())
            return ActionResult.PASS;

        return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
    }

    @Override
    public void updatePassengerPosition(Entity passenger)
    {
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }

    private void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater)
    {
        if (this.hasPassenger(passenger))
        {
            Vec3d v = new Vec3d(2, 0.0, 0.0).rotateY((float) (-Math.toRadians(this.getYaw())));
            v = v.add(getX(), getY() + 1, getZ());
            positionUpdater.accept(passenger, v.x, v.y, v.z);
        }
    }

    @Override
    public void onPassengerLookAround(Entity passenger)
    {
        super.onPassengerLookAround(passenger);
        this.setYaw(passenger.getYaw());
        this.setPitch(limitPitch(passenger.getPitch()));
    }

    //    @Override
//    public double getMountedHeightOffset()
//    {
//        return geth;
//    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    public void setParent(PhageRayBlockEntity phageRayBlockEntity)
    {
        this.parent = phageRayBlockEntity;
    }
}
