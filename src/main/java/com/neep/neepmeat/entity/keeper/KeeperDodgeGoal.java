package com.neep.neepmeat.entity.keeper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class KeeperDodgeGoal extends Goal
{
    protected long lastDodge;
    protected float range;
    protected int ticks;
    protected final KeeperEntity entity;

    public KeeperDodgeGoal(KeeperEntity entity, float range)
    {
//        super(mob, speed, false);
        this.entity = entity;
        this.range = range;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        return entity.getTarget() != null && entity.isPlayerStaring(entity.getTarget())
                && entity.getWorld().getTime() > lastDodge + 20;
    }

    @Override
    public void start()
    {
        super.start();
        lastDodge = entity.getWorld().getTime();
        ticks = 0;
    }

    @Override
    public void stop()
    {
        super.stop();
//        entity.getMoveControl().strafeTo(-2f, entity.getRandom().nextBoolean() ? 4f : -4f);
    }

    @Override
    public void tick()
    {
        super.tick();

//        entity.setSidewaysSpeed(entity.getRandom().nextBoolean() ? 1f : -2f);
        LivingEntity target = entity.getTarget();
        if (target != null)
        {
            boolean b1 = entity.getRandom().nextBoolean();

            float yaw = target.getYaw();
            Vec3d v = target.getPos().subtract(entity.getPos()).rotateY((float) (b1 ? Math.PI / 2 : -Math.PI));
            Vec3d newPos = target.getPos().add(v);

            boolean b2 = entity.getRandom().nextBoolean();
//            entity.getMoveControl().moveTo(entity.getX() + 4, entity.getY(), entity.getZ(), 40);
            entity.getMoveControl().moveTo(newPos.x, newPos.y, newPos.z, 40);
//            entity.getMoveControl().moveTo(entity.getX() + (b1 ? -4 : 4), entity.getY(), entity.getZ() + (b2 ? -4 : 4), 5);
//            entity.getMoveControl().strafeTo(-2f, entity.getRandom().nextBoolean() ? 4f : -4f);
        }
        ++ticks;
    }

    @Override
    public boolean canStop()
    {
        return ticks > 60;
    }

    @Override
    public boolean shouldContinue()
    {
        return ticks <= 60;
    }
}
