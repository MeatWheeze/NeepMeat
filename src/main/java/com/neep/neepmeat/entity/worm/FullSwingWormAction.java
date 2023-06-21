package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.Easing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class FullSwingWormAction implements WormAction
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "full_swing");

    protected final WormEntity parent;
    protected final int maxTicks;
    protected int age;

    // Time indices of attack eventss
    protected int[] timings =
            {
                    20,
                    60,
                    90
            };
    protected float angle;
    protected float finalAngle;
    protected float speed;
    protected boolean finished;
    protected float radius;

    public FullSwingWormAction(WormEntity entity)
    {
        this.parent = entity;
        this.maxTicks = 3 * 20;
        this.angle = entity.getYaw();
        this.finalAngle = angle - 360;
        this.speed = (float) -360 / (timings[1] - timings[0]);
        this.radius = 8;
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
        if (age >= timings[2]) this.finished = true;

        if (age > 0 && age < timings[0])
        {
            parent.lerpHeadPos(age, timings[0], getHeadPos(angle));
            parent.lerpHeadAngles(age, timings[0], 0, angle);
        }
        if (age > timings[0] && age < timings[1])
        {
            float dt = timings[1] - timings[0];

            angle = (float) (-360 * Easing.easeOutBack((age - timings[0]) / dt));

            apply(angle);
        }
        if (age == timings[1])
        {
            parent.setHeadAngles(0, angle + 360);
        }
        if (age > timings[1])
        {
            parent.returnHeadToNeutral(age - timings[1], timings[2] - timings[1]);
        }
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
}
