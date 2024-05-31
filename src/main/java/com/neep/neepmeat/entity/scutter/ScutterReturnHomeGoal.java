package com.neep.neepmeat.entity.scutter;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class ScutterReturnHomeGoal extends MoveToTargetPosGoal
{
    private final FarmingScutter scutter;
    private boolean reached;

    public ScutterReturnHomeGoal(FarmingScutter scutter, float speed, int range)
    {
        super(scutter, speed, range);
        this.scutter = scutter;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        if (cooldown > 0)
        {
            cooldown--;
        }
        else if (scutter.getHomePos() != null && (scutter.needsEmptying() || scutter.getTargets().isEmpty()))
        {
            cooldown = getInterval(mob);
            targetPos = scutter.getHomePos();
            return true;
        }
        return false;
    }

    @Override
    public void tick()
    {
        BlockPos blockPos = this.getTargetPos();
        Vec3d mobPos = mob.getPos();
        if (!blockPos.isWithinDistance(mobPos, this.getDesiredDistanceToTarget()))
        {
            this.reached = false;
            ++this.tryingTime;

            if (blockPos.isWithinDistance(mob.getPos(), 1.5))
            {
                mob.addVelocity(
                        0.1 * (blockPos.getX() + 0.5 - mobPos.x),
                        0,
                        0.1 * (blockPos.getZ() + 0.5 - mobPos.z));
            }

            if (this.shouldResetPath())
            {
                this.mob.getNavigation().startMovingAlong(mob.getNavigation().findPathTo(
                                blockPos.getX() + 0.5,
                                blockPos.getY(),
                                blockPos.getZ() + 0.5,
                                1), this.speed);
            }
        }
        else
        {
            this.reached = true;
            --this.tryingTime;
        }
    }

    protected boolean hasReached()
    {
        return this.reached;
    }

    @Override
    public void start()
    {
        super.start();
    }

    @Override
    protected int getInterval(PathAwareEntity mob)
    {
        return 20;
    }

    @Override
    public double getDesiredDistanceToTarget()
    {
        return 0.25;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos)
    {
        return pos.equals(scutter.getHomePos());
    }
}
