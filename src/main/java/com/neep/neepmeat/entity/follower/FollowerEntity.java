package com.neep.neepmeat.entity.follower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class FollowerEntity extends Entity
{
    public FollowerEntity(EntityType<?> type, World world)
    {
        super(type, world);
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
}
