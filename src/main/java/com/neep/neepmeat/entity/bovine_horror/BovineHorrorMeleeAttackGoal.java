package com.neep.neepmeat.entity.bovine_horror;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class BovineHorrorMeleeAttackGoal extends MeleeAttackGoal
{
    protected BovineHorrorEntity bovineHorror;

    public BovineHorrorMeleeAttackGoal(BovineHorrorEntity mob, double speed, boolean pauseWhenMobIdle)
    {
        super(mob, speed, pauseWhenMobIdle);
        this.bovineHorror = mob;
    }

    @Override
    public boolean canStart()
    {
        return super.canStart();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity)
    {
        return 8;
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
