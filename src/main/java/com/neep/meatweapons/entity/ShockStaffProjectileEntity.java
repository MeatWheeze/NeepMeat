package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ShockStaffProjectileEntity extends MeatgunProjectileEntity
{
    public ShockStaffProjectileEntity(World world)
    {
        this(MeatWeapons.SHOCK_STAFF_PROJECTILE, world);
        setNoGravity(true);
    }

    public ShockStaffProjectileEntity(EntityType<? extends MeatgunProjectileEntity> entityType, World world)
    {
        super(entityType, world);
        setNoGravity(true);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (collisions == 0 && random.nextFloat() < 0.7)
            getWorld().addParticle(
                    MWParticles.SHOCK_STAFF, getX(), getY(), getZ(),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3),
                    random.nextTriangular(0, 0.3));

        if (age > 500 || collidedTicks > 2 || distanceMoved > 60)
            discard();
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
        collideAndDiscard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        playSound(NMSounds.ZAP_HIT, 1, 1);
        collideAndDiscard();
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return NMSounds.SHOCK_STAFF_HIT;
    }

    protected void collideAndDiscard()
    {
        if (getWorld() instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(MWParticles.SHOCK_STAFF, getX(), getY(),getZ(), 10, 0.1, 0.1, 0.1, 0.1);
        }
    }

    @Override
    public void remove(RemovalReason reason)
    {
        super.remove(reason);
    }
}
