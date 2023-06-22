package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.Easing;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class WormFullSwingGoal extends AnimatedWormGoal<WormFullSwingGoal>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "full_swing");

    protected final WormEntity parent;

    protected float angle;
    protected float finalAngle;
    protected float radius;

    protected final Sequence<WormFullSwingGoal> orient = (action, counter) ->
    {
        if (counter > 20)
        {
            action.setSequence(action.swing);
            return;
        }
        if (action.parent.getTarget() == null)
        {
            action.setSequence(action.neutral);
        }

        action.parent.lerpHeadPos(counter, 20, getHeadPos(angle));
        action.parent.lerpHeadAngles(counter, 20, 0, angle);
    };

    protected final Sequence<WormFullSwingGoal> swing = (action, counter) ->
    {
        if (counter > 20)
        {
            action.setSequence(action.reset);
            return;
        }
        if (action.parent.getTarget() == null)
        {
            action.setSequence(action.neutral);
        }

        action.angle = (float) (-360 * Easing.easeOutBack(counter / 20f));

        apply(action.angle);

    };

    protected final Sequence<WormFullSwingGoal> reset = (action, counter) ->
    {
        apply(action.angle + 360);
        action.setSequence(action.neutral);
    };

    protected final Sequence<WormFullSwingGoal> neutral = (action, counter) ->
    {
        if (counter > 20)
        {
            action.markFinished();
            return;
        }

        action.parent.returnHeadToNeutral(counter, 20);
    };

    public WormFullSwingGoal(WormEntity entity, float radius)
    {
        this.parent = entity;
        this.angle = entity.getYaw();
        this.finalAngle = angle - 360;
        this.radius = radius;

        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        return parent.getTarget() != null;
    }

    @Override
    public void start()
    {
        super.start();
        setSequence(orient);
    }

    protected Vec3d getHeadPos(float angle)
    {
        return new Vec3d(
                parent.getX() + radius * Math.sin(Math.toRadians(angle)),
                parent.getY() + 5,
                parent.getZ() + radius * Math.cos(Math.toRadians(angle))
        );
    }

    protected void apply(float angle)
    {
        parent.setHeadAngles(0, angle);
        Vec3d headPos = getHeadPos(angle);
        parent.setHeadPos(headPos.x, headPos.y, headPos.z);
    }
}
