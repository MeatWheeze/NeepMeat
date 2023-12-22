package com.neep.neepmeat.entity.keeper;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.Vec3d;

public class KeeperRetreatGoal extends Goal
{
    protected final KeeperEntity entity;
    protected final double range;
    private final double fastSpeed;

    protected final EntityNavigation fleeingEntityNavigation;
    private Path fleePath;

    public KeeperRetreatGoal(KeeperEntity entity, float speed, int range)
    {
        this.entity = entity;
        this.range = range;
        this.fastSpeed = speed;
        this.fleeingEntityNavigation = entity.getNavigation();
    }

    @Override
    public boolean canStart()
    {
        if (entity.getAttacker() == null) return false;

        Vec3d vec3d = NoPenaltyTargeting.findFrom(entity, 16, 7, entity.getAttacker().getPos());
        if (vec3d == null)
        {
            return false;
        }

        this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
        return entity.shouldHeal();
    }


    @Override
    public void start()
    {
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, fastSpeed);
    }

    @Override
    public boolean shouldContinue()
    {
        return entity.getAttacker() != null && !this.fleeingEntityNavigation.isIdle();
    }

    @Override
    public void stop()
    {
        super.stop();
    }

//    protected void updatePos()
//    {
//        double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * range;
//        double y = entity.getY() + (entity.getRandom().nextDouble() - 0.5) * range;
//        double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * range;
//
//        foundPos = targetPos != null;
//        targetPos = getValidPos(x, y, z);
//    }
//
//    protected BlockPos getValidPos(double x, double y, double z)
//    {
//        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
//
//        while (mutable.getY() > entity.world.getBottomY() && !entity.world.getBlockState(mutable).getMaterial().blocksMovement())
//        {
//            mutable.move(Direction.DOWN);
//        }
//
//        BlockState blockState = entity.world.getBlockState(mutable);
//        if (blockState.getMaterial().blocksMovement()) return  null;
//
//        return new BlockPos(x, y, z);
//    }

    @Override
    public void tick()
    {
        if (entity.squaredDistanceTo(entity.getAttacker()) < 49.0)
        {
            entity.getNavigation().setSpeed(this.fastSpeed);
        }
    }
}
