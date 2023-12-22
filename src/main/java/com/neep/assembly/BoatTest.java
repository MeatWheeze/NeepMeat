package com.neep.assembly;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatTest extends BoatEntity
{
    public BoatTest(EntityType<? extends BoatEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public BoatTest(World world)
    {
        this(Assembly.BOAT_TEST, world);
    }

    @Override
    public void move(MovementType type, Vec3d movement)
    {
        super.move(type, movement);
        System.out.println(getPos());
    }
}
