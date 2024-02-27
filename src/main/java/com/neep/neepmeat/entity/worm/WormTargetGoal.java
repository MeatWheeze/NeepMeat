package com.neep.neepmeat.entity.worm;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class WormTargetGoal extends Goal
{
    protected final WormEntity parent;
    protected LivingEntity target;
    protected final TargetPredicate targetPredicate;
    protected final float range;

    public WormTargetGoal(WormEntity parent, TargetPredicate targetPredicate, float range)
    {
        this.parent = parent;
        this.targetPredicate = targetPredicate;
        this.range = range;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart()
    {
        return parent.getTarget() == null;
    }

    @Override
    public void start()
    {
        target = findTarget(getRange());
    }

    protected LivingEntity findTarget(float range)
    {
        Vec3d pos = parent.getPos();
        Box box = Box.of(pos, range * 2, range * 2, range * 2);
        List<LivingEntity> el = parent.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> true);
        return parent.getWorld().getClosestEntity(LivingEntity.class, targetPredicate, parent, pos.x, pos.y, pos.z, box);
    }

    @Override
    public boolean shouldContinue()
    {
        LivingEntity entity = parent.getTarget();
        if (entity == null) entity = this.target;

        if (entity == null || entity.isRemoved() || !targetPredicate.test(parent, entity)) return false;

        if (!parent.canTarget(entity))
        {
            return false;
        }
        AbstractTeam abstractTeam = parent.getScoreboardTeam();
        AbstractTeam abstractTeam2 = entity.getScoreboardTeam();
        if (abstractTeam != null && abstractTeam2 == abstractTeam)
        {
            return false;
        }
        double d = this.getRange();
        if (parent.squaredDistanceTo(entity) > d * d)
        {
            return false;
        }

        parent.setTarget(entity);
        return true;
    }

    private float getRange()
    {
        return range;
    }

    @Override
    public void stop()
    {
        parent.setTarget(null);
        this.target = null;
    }

    @Override
    public EnumSet<Control> getControls()
    {
        return super.getControls();
    }
}
