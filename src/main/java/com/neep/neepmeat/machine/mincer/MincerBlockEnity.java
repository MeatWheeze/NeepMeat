package com.neep.neepmeat.machine.mincer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.machine.death_blades.DeathBladesBlockEntity;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class MincerBlockEnity extends SyncableBlockEntity implements IMotorisedBlock
{
    private int damageTime;
    protected boolean running;

    protected WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        public boolean supportsInsertion()
        {
            return false;
        }
    };

    public MincerBlockEnity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MincerBlockEnity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MINCER, pos, state);
    }

    public void serverTick(World world)
    {
        if (running) processEntity(world);
    }

    protected void processEntity(World world)
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

    protected void damageEntity(ServerWorld world, LivingEntity entity)
    {
        float damageAmount = 1;
        if (entity.getHealth() <= damageAmount)
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            insertFluidFromEntity(this, entity);
            world.spawnParticles(NMParticles.MEAT_FOUNTAIN, pos.getX() + 0.5, pos.getY() + 1 + 14 / 16f, pos.getZ() + 0.5, 20, 0.4, 0.4, 0.4, 0.01);
        }
        else
        {
//            entity.damage(world.getDamageSources().generic(), damageAmount);
            entity.damage(DamageSource.GENERIC, damageAmount);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static void insertFluidFromEntity(MincerBlockEnity be, LivingEntity entity)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            be.getFluidStorage(null).insert(FluidVariant.of(NMFluids.STILL_TISSUE_SLURRY), DeathBladesBlockEntity.getEntityAmount(entity), transaction);
            transaction.commit();
        }
    }

    public void clientTickRunning(World world)
    {
        Random random = world.random;
        if (random.nextFloat() < 0.1f)
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

    public WritableSingleFluidStorage getFluidStorage(Direction direction)
    {
        return fluidStorage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("damageTime", damageTime);
        fluidStorage.toNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.damageTime = nbt.getInt("damageTime");
        fluidStorage.readNbt(nbt);
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        return false;
    }

    protected void updateBlockstate()
    {
        BlockState state = world.getBlockState(pos);
        if (state.get(MincerBlock.RUNNING) != running)
        {
            world.setBlockState(pos, state.with(MincerBlock.RUNNING, running));
        }
    }

    @Override
    public void setInputPower(float power)
    {
        running = power >= 0.1;
        updateBlockstate();
    }

    @Override
    public void onMotorRemoved()
    {
        running = false;
        updateBlockstate();
    }
}
