package com.neep.neepmeat.entity.scutter;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class ScutterReturnHomeGoal extends MoveToTargetPosGoal
{
    private final FarmingScutter scutter;

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
        return 0.5f;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos)
    {
        return pos.equals(scutter.getHomePos());
    }
}
