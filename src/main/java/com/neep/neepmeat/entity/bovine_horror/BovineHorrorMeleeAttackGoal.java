package com.neep.neepmeat.entity.bovine_horror;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class BovineHorrorMeleeAttackGoal extends Goal
{
    protected BovineHorrorEntity mob;
    protected int cooldown;
    private int maxCooldown = 20;

    public BovineHorrorMeleeAttackGoal(BovineHorrorEntity mob)
    {
//        this.setControls(EnumSet.of(Control.MOVE));
        this.mob = mob;
    }

    @Override
    public boolean canStart()
    {
        return
                mob.getTarget() != null
                && mob.canMelee(mob.getTarget());
    }

    @Override
    public boolean shouldContinue()
    {
        return canStart();
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
    public boolean shouldRunEveryTick()
    {
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (mob.getTarget() != null)
        {
            attack(mob.getTarget());
        }
        cooldown--;
    }

    protected void attack(LivingEntity target)
    {
        if (mob.canMelee(target) && this.cooldown <= 0)
        {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
        }
    }

    private void resetCooldown()
    {
        cooldown = maxCooldown;
    }
}
