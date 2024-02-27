package com.neep.neepmeat.entity.bovine_horror;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.EnumSet;

public class BHMoveToTargetGoal extends Goal
{
    protected final BovineHorrorEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;

    public BHMoveToTargetGoal(BovineHorrorEntity mob, double speed, boolean pauseWhenMobIdle)
    {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart()
    {
        long worldTime = this.mob.getWorld().getTime();
        if (worldTime - this.lastUpdateTime < 20L)
        {
            return false;
        }
        this.lastUpdateTime = worldTime;

        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || mob.isInRange(target))
        {
            return false;
        }

        this.path = this.mob.getNavigation().findPathTo(target, 0);
        if (this.path != null)
        {
            return true;
        }
        return this.getSquaredMaxAttackDistance(target) >= this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public boolean shouldContinue()
    {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || mob.isInRange(target))
        {
            return false;
        }

        if (!this.pauseWhenMobIdle)
        {
            return !this.mob.getNavigation().isIdle();
        }
        if (!this.mob.isInWalkTargetRange(target.getBlockPos()))
        {
            return false;
        }
        return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
    }

    @Override
    public void start()
    {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    @Override
    public void stop()
    {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity))
        {
            this.mob.setTarget(null);
        }
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick()
    {
        return true;
    }

    @Override
    public void tick()
    {
       LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null)
        {
            return;
        }
        this.mob.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
        if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f))
        {
            this.targetX = livingEntity.getX();
            this.targetY = livingEntity.getY();
            this.targetZ = livingEntity.getZ();
            this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
            if (d > 1024.0)
            {
                this.updateCountdownTicks += 10;
            }
            else if (d > 256.0)
            {
                this.updateCountdownTicks += 5;
            }
            if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed))
            {
                this.updateCountdownTicks += 15;
            }
            this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
        }
        this.cooldown = Math.max(this.cooldown - 1, 0);
//        this.attack(livingEntity, d);
    }

//    protected void attack(LivingEntity target, double squaredDistance)
//    {
//        double d = this.getSquaredMaxAttackDistance(target);
//        if (squaredDistance <= d && this.cooldown <= 0)
//        {
//            this.resetCooldown();
//            this.mob.swingHand(Hand.MAIN_HAND);
//            this.mob.tryAttack(target);
//        }
//    }

    protected void resetCooldown()
    {
        this.cooldown = this.getTickCount(20);
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity)
    {
        return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + entity.getWidth();
    }
}
