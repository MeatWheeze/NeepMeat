package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Optional;

public class BiteWormAction implements WormAction
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "bite");

    protected final WormEntity entity;
    protected final float range = 8;
    protected final int maxTicks;
    protected int age;
    protected LivingEntity target;

    protected int[] timings =
            {
                    (int) (0.70 * 20), // Attack start
                    (int) (0.84 * 20), // Attack end
                    (int) (1.5 * 20) // Action end
            };
    protected boolean finished;

    public BiteWormAction(WormEntity entity)
    {
        this.entity = entity;
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

        if (age < timings[0] && target != null)
        {
            // Orient the entity towards the target
            Vec2f vec = NMMaths.flatten(target.getPos().subtract(entity.getPos()));
            float angle = (float) (NMMaths.getAngle(vec) * (180f / Math.PI));
            entity.setYaw(angle);
        }
        else if (age == timings[1] && target != null)
        {
            Vec3d look = entity.getPos().add(entity.getRotationVec(0).multiply(5));
            if (entity.world instanceof ServerWorld serverWorld)
            {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, look.x, look.y, look.z, 3, 0.0, 0.0, 0.0, 0.0);
            }
            Box headBox = Box.of(look, 1, 1, 1);
            entity.world.getOtherEntities(entity, headBox).forEach(e ->
            {
                e.damage(DamageSource.mob(entity), 10);
            });
        }
    }

    protected LivingEntity findTarget()
    {
        Vec3d pos = entity.getPos();
        Box box = Box.of(pos, range * 2, range * 2, range * 2);
        return entity.world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, entity, pos.x, pos.y, pos.z, box);
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
