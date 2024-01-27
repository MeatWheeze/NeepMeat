package com.neep.neepmeat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class LimbEntity extends SimpleEntity
{
    private boolean squirm = true;

    public LimbEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (squirm)
        {
            setYaw((float) (Math.random() * 360));
            squirm = false;
        }

        tickMovement();
    }
}
