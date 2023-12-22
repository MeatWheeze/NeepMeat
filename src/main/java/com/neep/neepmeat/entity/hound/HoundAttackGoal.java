package com.neep.neepmeat.entity.hound;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class HoundAttackGoal extends MeleeAttackGoal
{
    private int ticks;

    public HoundAttackGoal(HoundEntity mob, double speed, boolean pauseWhenMobIdle)
    {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    public void start()
    {
        super.start();
        this.ticks = 0;
        this.mob.setAttacking(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        this.mob.setAttacking(false);
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.ticks;
//
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
