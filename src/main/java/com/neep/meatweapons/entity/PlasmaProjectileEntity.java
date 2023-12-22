package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class PlasmaProjectileEntity extends PersistentProjectileEntity
{
    int liveTime = 0;

    public PlasmaProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public PlasmaProjectileEntity(World world, double x, double y, double z, double vx, double vy, double vz)
    {
        super(MeatWeapons.PLASMA, x, y, z, world);
        this.setVelocity(vx, vy, vz);
        this.setNoGravity(true);
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

        ++liveTime;

        if (this.world.isClient)
        {
               this.spawnParticles(2);
        }
        else if (this.inGround || this.liveTime > 5 || this.getVelocity().length() < 1)
        {
            this.world.sendEntityStatus(this, (byte)0);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return NMSounds.FUSION_HIT;
    }

    private void spawnParticles(int amount)
    {
        this.world.addParticle(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
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
