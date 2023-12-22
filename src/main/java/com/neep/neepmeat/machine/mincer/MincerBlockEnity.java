package com.neep.neepmeat.machine.mincer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class MincerBlockEnity extends SyncableBlockEntity
{
    private int damageTime;

    public MincerBlockEnity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MincerBlockEnity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MINCER, pos, state);
    }

    public void serverTick()
    {
        processEntity();
    }

    protected void processEntity()
    {
        damageTime = Math.max(0, damageTime - 1);
        Box catchmentBox = new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 3.5, pos.getZ() + 1);
        List<MobEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), catchmentBox, e -> true);
        MobEntity entity;
        if (entities.size() > 0 && !(entity = entities.get(0)).isDead())
        {
            if (world.getRandom().nextInt(9) == 1)
            {
                ((ServerWorld) world).spawnParticles(ParticleTypes.LAVA, getPos().getX() + 0.5, getPos().getY() + 2, getPos().getZ() + 0.5,
                        1, 0.1, 0, 0.2, 0.01);
            }

//            MobEntity entity = entities.get(0);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5 + getYOffset(entity) * 1.4, pos.getZ() + 0.5);

            // This should rotate the entity, but it generally doesn't work, not sure why.
            entity.setYaw(MathHelper.wrapDegrees(entity.getYaw() + 10f));

            // Prevent the entity from shooting downwards and escaping/dying
            entity.fallDistance = 0;
            Vec3d vel = entity.getVelocity();
            entity.setVelocity(vel.x, 0, vel.z);

            // Damage entity
            if (damageTime == 0)
            {
                damageEntity((ServerWorld) world, entity);
                damageTime = 10;
            }
        }
    }

    void damageEntity(ServerWorld world, LivingEntity entity)
    {
        float damageAmount = 1;
        if (entity.getHealth() <= damageAmount)
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            world.spawnParticles(NMParticles.MEAT_FOUNTAIN, pos.getX(), pos.getY() + 1 + 14 / 16f, pos.getZ(), 20, 0.4, 0.4, 0.4, 0.01);
        }
        else
        {
            entity.damage(DamageSource.GENERIC, damageAmount);
        }
    }

    public void clientTick()
    {
        Random random = world.random;
        if (random.nextFloat() < 0.11f)
        {
            for (int i = 0; i < random.nextInt(2) + 2; ++i)
            {
                world.addParticle(ParticleTypes.SMOKE, (double) pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double) pos.getY() + 1 + 14 / 16f, (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
            }
        }
    }

    protected float getYOffset(LivingEntity entity)
    {
        return entity.getHealth() / entity.getMaxHealth();
    }
}
