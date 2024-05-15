package com.neep.neepmeat.entity.scutter;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ScutterEntity extends PathAwareEntity
{
    public ScutterEntity(EntityType<? extends ScutterEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    protected boolean shouldFollowLeash()
    {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state)
    {
    }


    @Override
    public boolean cannotDespawn()
    {
        return true;
    }

    @Override
    public boolean canTakeDamage()
    {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        return false;
    }
}
