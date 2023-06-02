package com.neep.neepmeat.block.machine;

import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.entity.MobPlatformRidingEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.UUID;

public class MobPlatformBlockEntity extends BlockEntity
{
    protected Entity passenger;
    protected UUID passengerUUID;

    protected final LazySupplier<MobPlatformRidingEntity> dummySupplier = LazySupplier.of(() ->
            new MobPlatformRidingEntity(getWorld(), getPos(), getCachedState(), this));

    public MobPlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MobPlatformBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MOB_PLATFORM, pos, state);
    }

    public void serverTick()
    {
        if (!world.isClient() && (passenger == null || passenger.isRemoved()) && passengerUUID != null)
        {
            // Retrieve the new entity instance after reload.
            passenger = ((ServerWorld) world).getEntity(passengerUUID);

            // If the entity is dead or properly removed, dump it.
            if (passenger == null)
                passengerUUID = null;
        }

        updatePassengerPosition(passenger);

        if (passenger != null && passenger.isSneaking())
        {
            ejectPassenger();
        }
    }

    public void clientTick()
    {
        if (passenger != null && passenger instanceof PlayerEntity playerEntity)
        {

        }
    }

    public boolean interact(LivingEntity entity)
    {
        if (entity != null && canRide(entity))
        {
            if (entity instanceof MobEntity mobEntity)
                mobEntity.detachLeash(true, true);

            setPassenger(entity);
            return true;
        }
        else if (hasPassenger())
        {
            ejectPassenger();
            return true;
        }
        return false;
    }

    public MobEntity captureLead(PlayerEntity player)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return world.getNonSpectatingEntities(MobEntity.class, new Box(i - 7.0, j - 7.0, k - 7.0, i + 7.0, j + 7.0, k + 7.0))
                .stream()
                .filter(mob -> mob.getHoldingEntity() == player).findFirst().orElse(null);
    }

    protected LivingEntity captureNearby()
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return world.getNonSpectatingEntities(MobEntity.class, new Box(i, j, k, i + 1.0, j + 1.0, k + 1.0))
                .stream()
                .findFirst().orElse(null);
    }

    public void setPassenger(Entity entity)
    {
        if (!hasPassenger())
        {
            entity.stopRiding();
            passenger = entity;
            passengerUUID = entity.getUuid();
//            entity.startRiding(dummySupplier.get());
            markDirty();
        }
    }

    public void ejectPassenger()
    {
//        dummySupplier.get().removeAllPassengers();
        removePassenger();
        markDirty();
    }

    public void removePassenger()
    {
        passenger = null;
        passengerUUID = null;
    }

    public boolean canRide(LivingEntity rider)
    {
        return !hasPassenger();
    }

    public boolean hasPassenger()
    {
        return passenger != null || passengerUUID != null;
    }

    protected void updatePassengerPosition(Entity passenger)
    {
        if (!this.hasPassenger())
        {
            return;
        }

        double d = pos.getY() + getMountedheight() + passenger.getHeightOffset();
        passenger.setPosition(pos.getX() + 0.5, d, pos.getZ() + 0.5);

//        if (!passenger.isAlive()) ejectPassenger();
    }

    private double getMountedheight()
    {
        return 0;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.contains("passengerUUID"))
            passengerUUID = nbt.getUuid("passengerUUID");
        else
            passengerUUID = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("argh", true);
        if (passenger != null)
           nbt.putUuid("passengerUUID", passenger.getUuid());

        if (passengerUUID != null)
            nbt.putUuid("passengerUUID", passengerUUID);
    }

    @Override
    public void markRemoved()
    {
        if (dummySupplier.isInitialised())
        {
            dummySupplier.get().remove(Entity.RemovalReason.DISCARDED);
        }
        super.markRemoved();
    }
}
