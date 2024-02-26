package com.neep.neepmeat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class LimbEntity extends SimpleEntity
{
    private boolean squirm = true;

    public LimbEntity(EntityType<? extends LimbEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
    }

    @Override
    public void tick()
    {
        super.tick();

        tickMovement();

        if (squirm)
        {
            setYaw((float) (Math.random() * 360));
            squirm = false;
        }

        if (age > 300)
        {
            remove(RemovalReason.DISCARDED);
        }
    }

    public boolean shouldSwim()
    {
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
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
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }
}
