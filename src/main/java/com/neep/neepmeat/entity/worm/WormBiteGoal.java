package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.Easing;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class WormBiteGoal extends AnimatedWormGoal<WormBiteGoal>
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "bite");

    protected final WormEntity parent;
    protected final float range = 16;

    protected final Sequence<WormBiteGoal> orientSequence;
    protected final Sequence<WormBiteGoal> attackSequence;
    protected final Sequence<WormBiteGoal> returnSequence;

    public WormBiteGoal(WormEntity entity)
    {
        this.parent = entity;
        this.setControls(EnumSet.of(Control.MOVE));

        orientSequence = (action, counter) ->
        {
            // If complete, start lunging. If there is no target, return to neutral.
            LivingEntity target = parent.getTarget();
            if (counter > 20)
            {
                action.setSequence(action.attackSequence);
                return;
            }
            if (target == null)
            {
                action.setSequence(action.returnSequence);
                return;
            }

            Vec3d neutralHead = parent.getNeutralHeadPos();
            Vec3d neutralToTarget = target.getPos().subtract(neutralHead);
            Vec3d baseToTarget = target.getPos().subtract(parent.getPos()).normalize();

            // Orient the entity towards the target
            Vec2f v1 = NMMaths.flattenY(baseToTarget);
            float angle = (float) Math.toDegrees(NMMaths.getAngle(v1));
            parent.setYaw(angle);

            // Determine the head position
            double targetRange = Math.min(7, baseToTarget.length());
            Vec3d headPos = neutralHead.add(baseToTarget.multiply(targetRange));

            // Reposition head
            parent.lerpHeadPos(counter, 20, headPos);
            Vec2f pitchYaw = NMMaths.rectToPol(neutralToTarget);
            parent.lerpHeadAngles(counter, 20, pitchYaw.x, pitchYaw.y);

        };

        attackSequence = (action, counter) ->
        {
            if (counter > 20)
            {
                action.setSequence(action.returnSequence);
                return;
            }
            LivingEntity target = parent.getTarget();
            if (target == null)
            {
                action.setSequence(action.returnSequence);
                return;
            }

            Vec3d neutralHead = parent.getNeutralHeadPos();
            Vec3d neutralToTarget = target.getPos().subtract(neutralHead);

            // Place the head some distance along the vector from the neutral position to the entity.
            double targetRange = Math.min(range, neutralToTarget.length());
            Vec3d headPos = neutralHead.add(neutralToTarget.normalize().multiply(targetRange));

            // Reposition head
            parent.lerpHeadPos(counter, 20, headPos, Easing::easeInBack);
            Vec2f pitchYaw = NMMaths.rectToPol(neutralToTarget);
            parent.lerpHeadAngles(counter, 20, pitchYaw.x, pitchYaw.y);
        };

        returnSequence = (action, counter) ->
        {
            if (counter > 20)
            {
                action.markFinished();
                return;
            }

            parent.returnHeadToNeutral(counter, 20);
        };
    }

    @Override
    public boolean canStart()
    {
        return parent.getTarget() != null;
    }

    @Override
    public void start()
    {
        setSequence(orientSequence);
        finished = false;
    }

    @Override
    public boolean shouldContinue()
    {
        return parent.getTarget() != null && !finished;
    }
}