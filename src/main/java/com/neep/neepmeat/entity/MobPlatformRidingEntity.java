package com.neep.neepmeat.entity;

import com.neep.neepmeat.block.machine.MobPlatformBlockEntity;
import com.neep.neepmeat.init.NMEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobPlatformRidingEntity extends Entity
{
    protected BlockPos blockEntityPos;
    protected MobPlatformBlockEntity be;

    public MobPlatformRidingEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    public MobPlatformRidingEntity(World world, BlockPos pos, BlockState state, MobPlatformBlockEntity mobPlatformBlockEntity)
    {
        this(NMEntities.MOB_PLATFORM, world);
        this.be = mobPlatformBlockEntity;
        this.blockEntityPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    protected void initDataTracker()
    {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {

    }

    @Override
    public void tick()
    {
        if (!(world.getBlockEntity(getBlockPos()) instanceof MobPlatformBlockEntity))
        {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("blockEntityPos", NbtHelper.fromBlockPos(blockEntityPos));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.blockEntityPos = NbtHelper.toBlockPos(nbt.getCompound("blockEntityPos"));
    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
        this.blockEntityPos = getBlockPos();
    }

    @Override
    public void updatePassengerPosition(Entity passenger)
    {
    }

    @Override
    public void removeAllPassengers()
    {
        super.removeAllPassengers();
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger)
    {
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    protected void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
        be.removePassenger();
    }

    public boolean canRide(Entity entity)
    {
        return !hasPassengers();
    }

    protected double getMountedheight()
    {
        return blockEntityPos.getY() + 2/ 16f;
    }
}
