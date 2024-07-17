package com.neep.meatweapons.entity;

import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MeatgunProjectileEntity extends PersistentProjectileEntity
{
    protected float homingRadius;
    protected float homingSpeed;
    @Nullable
    protected Entity homingTarget;

    protected int collisions = 0;
    protected int collidedTicks = 0;
    protected float distanceMoved = 0;

    public MeatgunProjectileEntity(EntityType<? extends MeatgunProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    public void tick()
    {
        double prevX = getX();
        double prevY = getY();
        double prevZ = getZ();
        super.tick();
        double dx = getX() - prevX;
        double dy = getY() - prevY;
        double dz = getZ() - prevZ;
        distanceMoved += Math.sqrt(dx * dx + dy * dy + dz * dz);

        tickHoming();

        if (collisions > 0)
            collidedTicks++;

        if (collisions == 0 && random.nextFloat() < 0.7)
            getWorld().addParticle(
                    MWParticles.SHOCK_STAFF, getX(), getY(), getZ(),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3));
    }

    protected void tickHoming()
    {
        if (getWorld().isClient())
            return;

        if (homingRadius > 0 && homingTarget == null)
        {
            if (age % 2 == 0)
            {
                Box box = Box.of(getPos(), homingRadius * 2, homingRadius * 2, homingRadius * 2);
                List<LivingEntity> entityList = new ArrayList<>();
                getWorld().collectEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, this::canHit, entityList, 7);

                double leastDistance = Double.MAX_VALUE;
                Entity target = null;
                for (var entity : entityList)
                {
                    double distance = entity.squaredDistanceTo(this);
                    if (distance < leastDistance)
                    {
                        leastDistance = distance;
                        target = entity;
                    }
                }

                if (target != null)
                {
                    this.homingTarget = target;
                }
            }
        }

        if (homingTarget != null)
        {
            Vec3d vel = getVelocity().multiply(0.8);

            Vec3d toTarget = new Vec3d(
                    homingTarget.getX() - getX(),
                    homingTarget.getEyeY() - getY(),
                    homingTarget.getZ() - getZ()
            ).normalize()
                    .multiply(homingSpeed >= 0 ? homingSpeed : speed);

            setVelocity(vel.add(toTarget));
        }
    }

    protected boolean canHit(Entity entity)
    {
        return super.canHit(entity) && !isOwner(entity);
    }

    @Override
    public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence)
    {
        super.setVelocity(shooter, pitch, yaw, roll, speed, divergence);

        if (homingSpeed <= 0)
            setHomingSpeed(speed / 2);
    }

    @Override
    protected ItemStack asItemStack()
    {
        return ItemStack.EMPTY;
    }

    protected void onCollision(HitResult hitResult)
    {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY)
        {
            this.onEntityHit((EntityHitResult) hitResult);
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        }
        else if (type == HitResult.Type.BLOCK)
        {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        collisions++;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
//        super.onBlockHit(blockHitResult);
        collisions++;
    }

    @Override
    public void playSound(@Nullable SoundEvent sound, float volume, float pitch)
    {
        if (sound == null)
            return;

        super.playSound(sound, volume, pitch);
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return NMSounds.SHOCK_STAFF_HIT;
    }

    public void setHomingRadius(float homingRadius)
    {
        this.homingRadius = homingRadius;
    }

    public void setHomingTarget(Entity target)
    {
        this.homingTarget = target;
    }

    public void setHomingSpeed(float homingSpeed)
    {
        this.homingSpeed = homingSpeed;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("homing_range", homingRadius);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.homingRadius = nbt.getFloat("homing_range");
    }
}
