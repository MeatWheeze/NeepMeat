package com.neep.meatweapons.entity;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.FusionCannonItem;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.network.ProjectileSpawnPacket;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

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
        setDamage(power / MAX_POWER * 5);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        this.dataTracker.startTracking(POWER, power);
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
//        DamageSource damageSource;
//        Entity entity2;
//        super.onEntityHit(entityHitResult);
//        Entity entity = entityHitResult.getEntity();
//        float f = (float)this.getVelocity().length();
//        int i = MathHelper.ceil(MathHelper.clamp((double)f * this.damage, 0.0, 2.147483647E9));
//        if (this.isCritical())
//        {
//            long l = this.random.nextInt(i / 2 + 2);
//            i = (int)Math.min(l + (long)i, Integer.MAX_VALUE);
//        }
//        if ((entity2 = this.getOwner()) == null) {
//            damageSource = DamageSource.arrow(this, this);
//        } else {
//            damageSource = DamageSource.arrow(this, entity2);
//            if (entity2 instanceof LivingEntity) {
//                ((LivingEntity)entity2).onAttacking(entity);
//            }
//        }
//        boolean bl = entity.getType() == EntityType.ENDERMAN;
//        int j = entity.getFireTicks();
//        if (this.isOnFire() && !bl) {
//            entity.setOnFireFor(5);
//        }
//        if (entity.damage(damageSource, i)) {
//            if (bl) {
//                return;
//            }
//            if (entity instanceof LivingEntity) {
//                Vec3d vec3d;
//                LivingEntity livingEntity = (LivingEntity)entity;
//                if (!this.world.isClient && this.getPierceLevel() <= 0) {
//                    livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
//                }
//                if (this.punch > 0 && (vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.punch * 0.6)).lengthSquared() > 0.0) {
//                    livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
//                }
//                if (!this.world.isClient && entity2 instanceof LivingEntity) {
//                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
//                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
//                }
//                this.onHit(livingEntity);
//                if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
//                    ((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
//                }
//                if (!entity.isAlive() && this.piercingKilledEntities != null) {
//                    this.piercingKilledEntities.add(livingEntity);
//                }
//                if (!this.world.isClient && entity2 instanceof ServerPlayerEntity) {
//                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
//                    if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
//                        Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, this.piercingKilledEntities);
//                    } else if (!entity.isAlive() && this.isShotFromCrossbow()) {
//                        Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity));
//                    }
//                }
//            }
//            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
//            if (this.getPierceLevel() <= 0) {
//                this.discard();
//            }
//        } else {
//            entity.setFireTicks(j);
//            this.setVelocity(this.getVelocity().multiply(-0.1));
//            this.setYaw(this.getYaw() + 180.0f);
//            this.prevYaw += 180.0f;
//            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
//                if (this.pickupType == PickupPermission.ALLOWED) {
//                    this.dropStack(this.asItemStack(), 0.1f);
//                }
//                this.discard();
//            }
//        }
        explode();
        spawnHitParticles();
        if (!world.isClient)
        {
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        explode();
        spawnHitParticles();
    }

    protected void explode()
    {
        float r = 2.5f;
        Vec3d pos = getPos();
        Box box = Box.of(pos, r, r ,r);
        List<Entity> entities = world.getOtherEntities(null, box);
        entities.forEach(e ->
        {
            // Damage reduces linearly with distance
            double d = getDamage() * (pos.distanceTo(e.getPos()) / r);
            e.damage(DamageSource.mobProjectile(this, getOwner() instanceof LivingEntity le ? le : null), (float) d);
        });
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
