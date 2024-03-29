package com.neep.neepmeat.entity.hound;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class HoundAttackGoal extends MeleeAttackGoal
{

    public HoundAttackGoal(HoundEntity mob, double speed, boolean pauseWhenMobIdle)
    {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    public void start()
    {
        super.start();
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
    }
}
