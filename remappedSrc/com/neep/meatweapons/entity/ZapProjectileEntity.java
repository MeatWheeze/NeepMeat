package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ZapProjectileEntity extends PersistentProjectileEntity
{

    public ZapProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public ZapProjectileEntity(World world, double x, double y, double z, double vx, double vy, double vz)
    {
        super(MeatWeapons.ZAP, x, y, z, world);
        this.setVelocity(vx, vy, vz);
        this.setNoGravity(false);
    }

    @Override
    public Packet createSpawnPacket()
    {
        return ProjectileSpawnPacket.create(this, MWNetwork.SPAWN_ID);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.world.isClient)
        {
               this.spawnParticles(2);
        }
        else if (this.inGround || this.distanceTraveled > 30 || this.getVelocity().length() < 1)
        {
            this.world.sendEntityStatus(this, (byte)0);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean hasNoGravity()
    {
        return true;
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return NMSounds.ZAP_HIT;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        spawnHitParticles();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        spawnHitParticles();
    }

    protected void spawnHitParticles()
    {
        for (int i = 0; i < 10; ++i)
        {
            this.world.addParticle(MWParticles.PLASMA_PARTICLE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    private void spawnParticles(int amount)
    {
        this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    protected void onHit(LivingEntity target)
    {
        super.onHit(target);
    }

    protected ItemStack asItemStack()
    {
            return new ItemStack(Items.ARROW);
    }
}
