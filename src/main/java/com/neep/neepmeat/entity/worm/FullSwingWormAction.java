package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
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

    protected final WormEntity entity;
    protected final int maxTicks;
    protected int age;

    // Time indices of attack eventss
    protected int[] timings =
            {
                    20,
                    40,
                    60
            };
    protected float angle;
    protected float finalAngle;
    protected float speed;
    protected boolean finished;

    public FullSwingWormAction(WormEntity entity)
    {
        this.entity = entity;
        this.maxTicks = 3 * 20;
        this.angle = entity.getYaw() - 60;
        this.finalAngle = angle - 360;
        this.speed = (float) -360 / (timings[1] - timings[0]);
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

        if (age > timings[0] && age < timings[1])
        {
            angle += speed;
            Vec3d centre = entity.getPos();
            Vec3d end = centre.add(Vec3d.fromPolar(0, angle).multiply(10));
            Box box = Box.of(centre, 15, 15, 15);
            entity.world.getOtherEntities(entity, box, e -> e instanceof LivingEntity).forEach(e ->
            {
                e.getBoundingBox().raycast(centre, end).ifPresent(v ->
                {
                    e.damage(DamageSource.mob(entity), 5);
                });
            });

            if (entity.world instanceof ServerWorld serverWorld)
            {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, end.x, end.y, end.z, 3, 0.0, 0.0, 0.0, 0.0);
            }
        }
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
