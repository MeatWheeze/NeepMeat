package com.neep.neepmeat.entity.bovine_horror;

import com.neep.meatweapons.Util;
import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.entity.goal.AnimatedGoal;
import com.neep.neepmeat.init.NMSounds;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;

public class BHBeamGoal extends AnimatedGoal<BovineHorrorEntity, BHBeamGoal>
{
    private final BovineHorrorEntity mob;

    protected final Sequence<BHBeamGoal> orient = (action, counter) ->
    {
        var mob = action.mob;

        if (mob.getTarget() == null)
        {
            markFinished();
            return;
        }

        if (counter == 0)
        {
            mob.syncNearby("animation.horror.beam");
            mob.playSound(NMSounds.BH_CHARGE, 1, 0.9f);
            mob.getWorld().playSound(null, mob.getX(), mob.getY(), mob.getZ(), NMSounds.BH_PHASE2, SoundCategory.HOSTILE, 5, 1);
        }

        mob.setVisibility(1);

        if (counter > 10 && counter % 2 == 0)
        {
            if (mob.getWorld() instanceof ServerWorld serverWorld)
            {
                Vec3d origin = getOrigin();
                serverWorld.spawnParticles(MWParticles.PLASMA_PARTICLE, origin.x, origin.y, origin.z,
                        10, 1, 1, 1, 1);
            }
        }

        if (counter > 40)
        {
            setSequence(action.shootBeams);
        }
    };

    protected final Sequence<BHBeamGoal> shootBeams = (action, counter) ->
    {
        LivingEntity target = action.mob.getTarget();
        if (target != null && counter < 20 && counter % 2 == 0)
        {
            Vec3d origin = getOrigin();
            Vec3d toEnd = target.getPos().subtract(origin).multiply(25);
            var random = action.mob.getRandom();
            Vec3d end = origin.add(
                    toEnd.x + random.nextTriangular(0, 20),
                    toEnd.y + random.nextTriangular(0, 20) + target.getHeight() / 2,
                    toEnd.z + random.nextTriangular(0, 20)
            );
            shootBeam(origin, end);
        }
        if (counter > 40)
        {
            markFinished();
        }
    };

    protected Vec3d getOrigin()
    {
        return getMob().getPos().add(0, 4, 0);
    }

    protected void shootBeam(Vec3d origin, Vec3d end)
    {
        var foundTarget = hitScan(mob, origin, end, 30);
        DamageSource damageSource = mob.getWorld().getDamageSources().mobAttack(mob);

        if (foundTarget.first() != null)
        {
            foundTarget.first().damage(damageSource, 4);
        }

        PlayerLookup.tracking(getMob()).forEach(p ->
        {
            MWGraphicsEffects.syncBeamEffect(p, MWGraphicsEffects.BEAM, getMob().getWorld(),
                    origin, foundTarget.second(), Vec3d.ZERO, 1f, 20);
        });

    }

    protected Pair<Entity, Vec3d> hitScan(LivingEntity caster, Vec3d start, Vec3d end, double distance)
    {
        World world = caster.getWorld();

        // Find where the ray hits a block
        RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
        BlockHitResult blockResult = world.raycast(ctx);

        Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.canHit();

        double minDistance = distance;
        Entity entity = null;
        EntityHitResult entityResult = null;
        for (EntityHitResult result : Util.getRayTargets(caster, start, blockResult.getPos(), entityFilter, 0.1))
        {
            if (result.getPos().distanceTo(start) < minDistance)
            {
                minDistance = result.getPos().distanceTo(start);
                entity = result.getEntity();
                entityResult = result;
            }
        }

        Vec3d hitPos = Objects.requireNonNullElse(entityResult, blockResult).getPos();

        return Pair.of(entity, hitPos);
    }

    public BHBeamGoal(BovineHorrorEntity mob)
    {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public boolean shouldContinue()
    {
        return super.shouldContinue() && canStart();
    }

    @Override
    public void start()
    {
        super.start();
        setSequence(orient);
    }

    protected BovineHorrorEntity getMob()
    {
        return mob;
    }
}
