package com.neep.neepmeat.entity.bovine_horror;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class HorrorTeleportGoal extends Goal
{
    private final BovineHorrorEntity mob;
    private int attempts = 0;
    private boolean teleported;
    private final int maxAttempts;
    private final double targetRange;

    private long lastTeleport;
    private final int cooldown;

    public HorrorTeleportGoal(@NotNull BovineHorrorEntity mob, int maxAttempts, double targetRange, int cooldown)
    {
        this.mob = mob;
        this.maxAttempts = maxAttempts;
        this.targetRange = targetRange;
        this.cooldown = cooldown;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        if (!mob.isOnGround() && mob.world.getTime() - lastTeleport < cooldown)
        {
            return false;
        }

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive())
        {
            return false;
        }

        double dist = target.distanceTo(mob);
        if (dist > targetRange)
        {
            return true;
        }
        return false;
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
            Vec3d targetPos = target.getPos();
            double currentDistance = mob.distanceTo(target);

            double r = Math.random() * targetRange;
            double t = Math.random() * Math.PI * 2;

            // Get the mob's box centred at 0 0 0
            Box originBox = mob.getBoundingBox().offset(mob.getPos());
            BlockPos.Mutable mutable = new BlockPos.Mutable(targetPos.x + r * Math.sin(t), targetPos.y, targetPos.z + r * Math.cos(t));
            int count = 0;
            while (!mob.world.isSpaceEmpty(originBox.offset(mutable)))
            {
                mutable.set(mutable, Direction.UP);
                count++;

                if (count >= targetRange)
                {
                    return;
                }
            }

            Vec3d newPos =  new Vec3d(mutable.getX(), mutable.getY(), mutable.getZ());

            double newDistance = targetPos.distanceTo(newPos);

            if (newDistance < currentDistance)
            {
                mob.requestTeleport(newPos.x, newPos.y, newPos.z);
                lastTeleport = mob.world.getTime();
                teleported = true;
            }
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
