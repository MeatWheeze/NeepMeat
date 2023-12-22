package com.neep.neepmeat.entity.keeper;

import com.neep.meatweapons.item.IGunItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;

import java.util.EnumSet;

public class KeeperRangedAttackGoal<T extends HostileEntity & RangedAttackMob> extends Goal
{
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;
    protected Item item;

    public KeeperRangedAttackGoal(T actor, double speed, int attackInterval, float range, Item item)
    {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.item = item;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public void setAttackInterval(int attackInterval)
    {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canStart()
    {
        if (actor.getTarget() == null)
        {
            return false;
        }
        return this.isHoldingItem();
    }

    protected boolean isHoldingItem()
    {
        return actor.isHolding(item);
//        return actor.isHolding(stack -> stack.getItem() instanceof IGunItem);
    }

    @Override
    public boolean shouldContinue()
    {
        return (this.canStart() || !actor.getNavigation().isIdle()) && isHoldingItem();
    }

    @Override
    public void start()
    {
        super.start();
        actor.setAttacking(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        actor.setAttacking(false);
        targetSeeingTicker = 0;
        cooldown = -1;
        actor.clearActiveItem();
    }

    @Override
    public boolean shouldRunEveryTick()
    {
        return true;
    }

    @Override
    public void tick()
    {
        LivingEntity target = actor.getTarget();

        if (target == null) return;

        double dist = actor.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        boolean canSee = actor.getVisibilityCache().canSee(target);
        boolean seen = targetSeeingTicker > 0;

        if (canSee != seen)
        {
            targetSeeingTicker = 0;
        }

        if (canSee)
        {
            ++targetSeeingTicker;
        }
        else
        {
            --targetSeeingTicker;
        }

        if (dist > squaredRange || targetSeeingTicker < 20)
        {
            actor.getNavigation().startMovingTo(target, speed);
            combatTicks = -1;
        }
        else
        {
            actor.getNavigation().stop();
            ++combatTicks;
        }

        if (combatTicks >= 20)
        {
            if (actor.getRandom().nextFloat() < 0.3)
            {
                movingToLeft = !movingToLeft;
            }
            if (actor.getRandom().nextFloat() < 0.3)
            {
                backward = !backward;
            }
            combatTicks = 0;
        }

        if (combatTicks > -1)
        {
            if (dist > squaredRange * 0.75f)
            {
                backward = false;
            }
            else if (dist < squaredRange * 0.25f)
            {
                backward = true;
            }
            actor.getMoveControl().strafeTo(backward ? -0.5f : 0.5f, movingToLeft ? 0.5f : -0.5f);
            actor.lookAtEntity(target, 30.0f, 30.0f);
        }
        else
        {
            actor.getLookControl().lookAt(target, 30.0f, 30.0f);
        }

        if (actor.isUsingItem())
        {
            int i;
            if (!canSee && targetSeeingTicker < -60)
            {
                actor.clearActiveItem();
            }
            else if (canSee && (i = actor.getItemUseTime()) >= 20)
            {
                actor.clearActiveItem();
                actor.attack(target, BowItem.getPullProgress(i));
                cooldown = attackInterval;
            }
        }
        else if (--cooldown <= 0 && targetSeeingTicker >= -60)
        {
            actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(actor, item));
        }
    }
}