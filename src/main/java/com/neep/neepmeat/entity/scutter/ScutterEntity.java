package com.neep.neepmeat.entity.scutter;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class ScutterEntity extends PathAwareEntity
{
    public ScutterEntity(EntityType<? extends ScutterEntity> type, World world)
    {
        super(type, world);
    }

}
