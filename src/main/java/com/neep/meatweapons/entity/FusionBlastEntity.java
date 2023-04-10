package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.FusionCannonItem;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class FusionBlastEntity extends PersistentProjectileEntity
{

    private static final TrackedData<Float> POWER = DataTracker.registerData(FusionBlastEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected float power;
    public static float MAX_POWER = FusionCannonItem.MAX_CHARGE_TIME / 2f;

    public FusionBlastEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
    {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public FusionBlastEntity(World world, double x, double y, double z, double vx, double vy, double vz, float power)
    {
        this(MeatWeapons.FUSION_BLAST, world);
        this.setPosition(x, y, z);
        this.setVelocity(vx, vy, vz);
        this.power = power;
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        this.dataTracker.startTracking(POWER, power);
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
        dataTracker.set(POWER, power);
        if (this.world.isClient)
        {
               this.spawnParticles();
        }
        else if (this.inGround || this.distanceTraveled > 30 || getVelocity().length() < 0.1)
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
        if (world instanceof ServerWorld serverWorld)
        {
            for (int i = 0; i < 10; ++i)
            {
//                this.world.addParticle(MWParticles.PLASMA_PARTICLE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                serverWorld.spawnParticles(MWParticles.PLASMA_PARTICLE, getX(), getY(), getZ(), 15, 0.1, 0.1, 0.1, 0.1);
            }
        }
    }

    protected void spawnParticles()
    {
        this.world.addParticle(MWParticles.PLASMA_PARTICLE,
                this.getX() + (random.nextFloat() - 0.5) / 2,
                this.getY() + (random.nextFloat() - 0.5) / 2,
                this.getZ() + (random.nextFloat() - 0.5) / 2,
                0, 0, 0);
    }

    @Override
    protected void onHit(LivingEntity target)
    {
        super.onHit(target);
    }

    @Override
    protected ItemStack asItemStack()
    {
        return ItemStack.EMPTY;
    }

    public float getPower()
    {
        return dataTracker.get(POWER);
    }
}
