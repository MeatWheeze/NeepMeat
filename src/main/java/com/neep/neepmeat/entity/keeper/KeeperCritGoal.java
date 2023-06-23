package com.neep.neepmeat.entity.keeper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class KeeperCritGoal extends Goal
{
    protected int ticks;
    protected float range;
    protected int cooldown;
    protected final KeeperEntity entity;

    public KeeperCritGoal(KeeperEntity entity, float range)
    {
//        super(mob, speed, false);
        this.entity = entity;
        this.range = range;
        this.setControls(EnumSet.of(Control.JUMP));
    }

//    @Override
//    protected int getMaxCooldown()
//    {
//        return this.getTickCount(maxCooldown);
//    }

    @Override
    public boolean canStart()
    {
        return entity.getTarget() != null;
    }

    @Override
    public void start()
    {
        super.start();
        this.ticks = 0;
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.ticks;
        LivingEntity target = entity.getTarget();
        cooldown = Math.max(this.cooldown - 1, 0);
        if (cooldown == 0 && target != null && entity.isInRange(target, range))
        {
            if (entity.squaredDistanceTo(target) < entity.meleeAttackGoal.getSquaredMaxAttackDistance(target))
            {
                entity.getJumpControl().setActive();
                cooldown = getTickCount(25);
            }
        }
//        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2)
//        {
//            this.mob.setAttacking(true);
//        }
//        else
//        {
//            this.mob.setAttacking(false);
//        }
    }
}
