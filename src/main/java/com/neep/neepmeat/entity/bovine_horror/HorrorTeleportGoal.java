package com.neep.neepmeat.entity.bovine_horror;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class HorrorTeleportGoal extends Goal
{
    private final BovineHorrorEntity mob;
    private int attempts = 0;
    private boolean teleported;
    private int maxAttempts;

    public HorrorTeleportGoal(BovineHorrorEntity mob, int maxAttempts)
    {
        this.mob = mob;
        this.maxAttempts = maxAttempts;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() && !mob.isInMoveRange(target))
        {
            return false;
        }
        return true;
    }

    @Override
    public void start()
    {
        super.start();
        attempts = 0;
        teleported = false;
    }

    @Override
    public void tick()
    {
        ++attempts;

        LivingEntity target = mob.getTarget();
        if (target != null)
        {
            double currentDistance = mob.distanceTo(target);

//            Vec3d newPos =
        }
    }

    @Override
    public boolean shouldContinue()
    {
        return canStart() && attempts < maxAttempts && !teleported;
    }

    protected static Vec3d findTeleportPos(World world, Vec3d v, Entity mob)
    {
        BlockPos.Mutable mutable = new BlockPos(v).mutableCopy();
        while (world.getBlockState(mutable).getMaterial().blocksMovement())
        {
            mutable.set(mutable, Direction.UP);
        }
        return new Vec3d(v.x, mutable.getY(), v.z);
    }
}
