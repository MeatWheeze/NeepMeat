package com.neep.meatweapons.entity;

import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class MeatgunProjectileEntity extends PersistentProjectileEntity
{
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

        if (collisions > 0)
            collidedTicks++;

        if (collisions == 0 && random.nextFloat() < 0.7)
            getWorld().addParticle(
                    MWParticles.SHOCK_STAFF, getX(), getY(), getZ(),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3));

        if (age > 500 || collidedTicks > 2 || distanceMoved > 60)
            discard();
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
}
