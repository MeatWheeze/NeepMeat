package com.neep.neepmeat.entity;

import com.neep.neepmeat.entity.follower.FollowerEntity;
import com.neep.neepmeat.init.NMEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class LimbEntity extends SimpleEntity
{
    private final int maxAge;
    public boolean squirm = false;

    public LimbEntity(EntityType<? extends LimbEntity> entityType, World world)
    {
        super(entityType, world);
        maxAge = 300;
    }

    public LimbEntity(World world, int maxAge, boolean squirm)
    {
        super(NMEntities.LIMB, world);
        this.maxAge = maxAge;
        this.squirm = squirm;
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

        if (age > maxAge)
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
    public boolean collidesWith(Entity other)
    {
        return !(other instanceof FollowerEntity);
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
        // I can't work out how to make the Follower not collide with any entity.
        if (entity instanceof FollowerEntity)
            return;

        super.pushAwayFrom(entity);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }
}
