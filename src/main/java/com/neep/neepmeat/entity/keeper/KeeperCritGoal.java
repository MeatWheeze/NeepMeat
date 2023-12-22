package com.neep.neepmeat.entity.keeper;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class KeeperCritGoal extends MeleeAttackGoal
{
    protected int ticks;
    protected int maxCooldown;

    public KeeperCritGoal(PathAwareEntity mob, double speed, int cooldown)
    {
        super(mob, speed, false);
        this.maxCooldown = cooldown;
    }

    @Override
    protected int getMaxCooldown()
    {
        return this.getTickCount(maxCooldown);
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
        this.mob.setAttacking(false);
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2)
        {
            this.mob.setAttacking(true);
        }
        else
        {
            this.mob.setAttacking(false);
        }
    }
}
