package com.neep.meatweapons.entity;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.network.MWNetwork;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ExplodingShellEntity extends PersistentProjectileEntity
{

    protected int explosionPower = 1;
    protected boolean destructive;

    public ExplodingShellEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public ExplodingShellEntity(World world, int explosionPower, boolean destructive, double x, double y, double z, double vx, double vy, double vz)
    {
        super(MeatWeapons.EXPLODING_SHELL, x, y, z, world);
        this.setVelocity(vx, vy, vz);
        this.setNoGravity(false);
        this.explosionPower = explosionPower;
        this.destructive = destructive;
    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return ProjectileSpawnPacket.create(this, MWNetwork.SPAWN_ID);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.world.isClient)
        {
               this.spawnParticles(5);
        }
        else if (this.inGround || this.distanceTraveled > 200)
        {
//            explode();
            this.world.sendEntityStatus(this, (byte) 0);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult)
    {
        super.onCollision(hitResult);
        if (!world.isClient)
        {
            if (hitResult.getType() == HitResult.Type.BLOCK)
            {
                explode();
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        if (!world.isClient)
        {
            explode();
        }
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return SoundEvents.BLOCK_STONE_HIT;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ExplosionPower", (byte) this.explosionPower);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("ExplosionPower", 99))
        {
            this.explosionPower = nbt.getByte("ExplosionPower");
        }
    }

    protected void explode()
    {
        boolean mobGriefing = world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && destructive;
        this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), this.explosionPower, mobGriefing, mobGriefing ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
    }

    private void spawnParticles(int amount)
    {
//        Random rand = new Random((long) getX());
//        System.out.println(distanceTraveled);
        double x = 0.05;
        for (int i = 0; i < amount; ++i)
            this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), x * random.nextGaussian(), x * random.nextGaussian(), x * random.nextGaussian());
    }

    protected void onHit(LivingEntity target)
    {
        super.onHit(target);
    }

    protected ItemStack asItemStack()
    {
            return new ItemStack(MWItems.BALLISTIC_CARTRIDGE);
    }
}
