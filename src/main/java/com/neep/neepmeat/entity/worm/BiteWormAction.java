package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.Easing;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class BiteWormAction implements WormAction
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "bite");

    protected final WormEntity parent;
    protected final float range = 16;
    protected final int maxTicks;
    protected int age;
    protected LivingEntity target;
    protected State state;

    protected int[] timings =
            {
                    (int) (20), // Attack start
                    (int) (40), // Attack end
                    (int) (60) // Action end
            };
    protected boolean finished;

    public BiteWormAction(WormEntity entity)
    {
        this.parent = entity;
        this.maxTicks = timings[2];
        target = findTarget();
    }

    @Override
    public Identifier getId()
    {
        return ID;
    }

    @Override
    public void tick()
    {
        ++age;
        if (age >= maxTicks) this.finished = true;
        target = findTarget();

        // Skip iteration and wait for a target for half a second
        if (state == State.NO_TARGET && age < 10) return;

        if (state == State.TARGET)
        {
            Vec3d baseToTarget = target.getPos().subtract(parent.getPos()).normalize();
            Vec3d neutralHead = parent.getNeutralHeadPos();
            Vec3d neutralToTarget = target.getPos().subtract(neutralHead);

            if (age < timings[0])
            {
                // Orient the entity towards the target
                Vec2f v1 = NMMaths.flattenY(baseToTarget);
                float angle = (float) Math.toDegrees(NMMaths.getAngle(v1));
                parent.setYaw(angle);

                // Determine the head position
                double targetRange = Math.min(7, baseToTarget.length());
                Vec3d headPos = neutralHead.add(baseToTarget.multiply(targetRange));

                // Reposition head
                parent.lerpHeadPos(age, timings[0], headPos);
                Vec2f pitchYaw = NMMaths.rectToPol(neutralToTarget);
                parent.lerpHeadAngles(age, timings[0], pitchYaw.x, pitchYaw.y);
            }
            else if (age < timings[1])
            {
                // Place the head 7 blocks along the vector from the neutral position to the entity
                double targetRange = Math.min(range, neutralToTarget.length());
                Vec3d headPos = neutralHead.add(neutralToTarget.normalize().multiply(targetRange));

                // Reposition head
                parent.lerpHeadPos(age - timings[0], timings[1] - timings[0], headPos, Easing::easeInBack);
                Vec2f pitchYaw = NMMaths.rectToPol(neutralToTarget);
                parent.lerpHeadAngles(age - timings[0], timings[1] - timings[0], pitchYaw.x, pitchYaw.y);
            }
            else
            {
                parent.returnHeadToNeutral(age - timings[1], timings[2] - timings[1]);
            }
        }
        else
        {
            parent.returnHeadToNeutral(age - timings[1], timings[2] - timings[1]);
        }
    }

    protected LivingEntity findTarget()
    {
        // Don't bother picking a new target if the current one is in range.
        if (target != null && parent.distanceTo(target) < range / 2) return target;

        Vec3d pos = parent.getPos();
        Box box = Box.of(pos, range * 2, range * 2, range * 2);
        List<LivingEntity> el = parent.world.getEntitiesByClass(LivingEntity.class, box, e -> true);
        return parent.world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, parent, pos.x, pos.y, pos.z, box);
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
    }

    protected enum State
    {
        TARGET, NO_TARGET;
    }
}
