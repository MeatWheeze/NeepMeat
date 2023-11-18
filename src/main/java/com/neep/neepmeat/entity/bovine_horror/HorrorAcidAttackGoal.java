package com.neep.neepmeat.entity.bovine_horror;

import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.network.ParticleSpawnPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class HorrorAcidAttackGoal extends Goal
{
    protected BovineHorrorEntity mob;
    protected int ticks = 0;
    protected boolean spawned = false;

    public HorrorAcidAttackGoal(BovineHorrorEntity mob)
    {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE));
    }
    @Override
    public boolean canStart()
    {
        if (mob.getTarget() != null)
        {
            double dist = mob.getTarget().getPos().distanceTo(mob.getPos());
            return dist >= 7;
        }
        return false;
    }

    @Override
    public void start()
    {
        ticks = 0;
        spawned = false;
    }

    @Override
    public boolean shouldContinue()
    {
        return (mob.getTarget() != null && ticks < 100);
    }

    @Override
    public boolean canStop()
    {
        return ticks <= 20 || ticks >= 100;
    }

    @Override
    public void stop()
    {
    }


    @Override
    public void tick()
    {
        super.tick();
        ++ticks;

        World world = mob.getWorld();

        LivingEntity target = mob.getTarget();
        if (ticks > 30 && ticks < 80 && (world.getTime() & 2) == 0)
        {
//            Vec3d toTarget = new Vec3d(Math.sin(mob.getYaw()), 0.1, Math.cos(mob.getY()));
            Vec3d toTarget = new Vec3d(0, 0.1, 0);
            Vec3d spread = new Vec3d(1, 1, 1);
            Vec3d origin = new Vec3d(mob.getX(), mob.getY() + 3, mob.getZ());

            for (ServerPlayerEntity player : PlayerLookup.tracking(mob))
            {
                ParticleSpawnPacket.send(player, NMParticles.BODY_COMPOUND_SHOWER, origin, toTarget, spread, 10);
            }
        }

        if (ticks > 40 && ticks < 60 && !spawned)
        {
            Vec3d toTarget = target.getPos().subtract(mob.getPos());

            var projectile = NMEntities.ACID_SPRAY.create(world);
            world.spawnEntity(projectile);
            float speed = toTarget.length() > 20 ? 2 : 0.5f;
            projectile.target(new Vec3d(mob.getX(), mob.getY() + 4, mob.getZ()),
                    target.getPos(), speed, 1);
            projectile.setOwner(mob);

            spawned = true;
        }
    }
}
