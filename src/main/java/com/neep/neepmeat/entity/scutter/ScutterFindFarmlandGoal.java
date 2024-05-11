package com.neep.neepmeat.entity.scutter;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class ScutterFindFarmlandGoal extends Goal
{
    private final FarmingScutter entity;
    private final int range;
    private final int maxYDiff;
    private BlockPos targetPos;

    public ScutterFindFarmlandGoal(FarmingScutter entity, int range, int maxYDiff)
    {
        this.entity = entity;

        this.range = range;
        this.maxYDiff = maxYDiff;
    }

    @Override
    public boolean canStart()
    {
        return false;
    }

    protected boolean findTargetPos()
    {
        BlockPos blockPos = entity.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int k = 0; k <= this.maxYDiff; k = k > 0 ? -k : 1 - k)
        {
            for (int l = 0; l < this.range; ++l)
            {
                for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m)
                {
                    for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n)
                    {
                        mutable.set(blockPos, m, k - 1, n);
//                        if (entity.isInWalkTargetRange(mutable) && this.isTargetPos(this.entity.getWorld(), mutable))
                        {
                            this.targetPos = mutable.toImmutable();
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
