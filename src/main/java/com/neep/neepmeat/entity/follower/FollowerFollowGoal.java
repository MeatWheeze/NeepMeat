package com.neep.neepmeat.entity.follower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FollowerFollowGoal extends Goal
{
    private final MobEntity mob;
    private final double speed;
    private final EntityNavigation navigation;
    private final float minDistance;
    @Nullable private Entity target;
    private int updateCountdownTicks;
    private float oldWaterPathFindingPenalty;

    public FollowerFollowGoal(MobEntity mob, double speed, float minDistance)
    {
        this.mob = mob;
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(mob.getNavigation() instanceof MobNavigation) && !(mob.getNavigation() instanceof BirdNavigation))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean canStart()
    {
        Entity mobTarget = mob.getTarget();
        if (mobTarget != null && !mobTarget.isInvisible())
        {
            this.target = mobTarget;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinue()
    {
        return this.target != null && !this.navigation.isIdle() && this.mob.squaredDistanceTo(this.target) > (double) (this.minDistance * this.minDistance);
    }

    @Override
    public void start()
    {
        this.updateCountdownTicks = 0;
        this.oldWaterPathFindingPenalty = this.mob.getPathfindingPenalty(PathNodeType.WATER);
        this.mob.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop()
    {
        this.target = null;
        this.navigation.stop();
        this.mob.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathFindingPenalty);
    }

    @Override
    public void tick()
    {
        if (this.target != null && !this.mob.isLeashed())
        {
            this.mob.getLookControl().lookAt(this.target, 10.0F, (float) this.mob.getMaxLookPitchChange());
            if (--this.updateCountdownTicks <= 0)
            {
                this.updateCountdownTicks = this.getTickCount(10);
                double d = this.mob.getX() - this.target.getX();
                double e = this.mob.getY() - this.target.getY();
                double f = this.mob.getZ() - this.target.getZ();
                double g = d * d + e * e + f * f;
                if (!(g <= (double) (this.minDistance * this.minDistance)))
                {
                    this.navigation.startMovingTo(this.target, this.speed);
                }
                else
                {
                    this.navigation.stop();
                    double h = this.target.getX() - this.mob.getX();
                    double i = this.target.getZ() - this.mob.getZ();
                    this.navigation.startMovingTo(this.mob.getX() - h, this.mob.getY(), this.mob.getZ() - i, this.speed);
                }
            }
        }
    }
}
