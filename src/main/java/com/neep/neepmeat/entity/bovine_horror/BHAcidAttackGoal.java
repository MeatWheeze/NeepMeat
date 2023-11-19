package com.neep.neepmeat.entity.bovine_horror;

import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.ParticleSpawnPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BHAcidAttackGoal extends Goal
{
    protected BovineHorrorEntity mob;
    protected int ticks = 0;
    protected int spawned = 0;
    protected int maxSpawned;
    protected int cooldown = 0;

    public BHAcidAttackGoal(BovineHorrorEntity mob, float size, int maxSpawned)
    {
        this.mob = mob;
        this.maxSpawned = maxSpawned;
//        this.setControls(EnumSet.of(Control.MOVE));
    }
    @Override
    public boolean canStart()
    {
        LivingEntity target = mob.getTarget();
        if (target != null && target.isAlive())
        {
            return !mob.canMelee(target);
//            double dist = mob.getTarget().getPos().distanceTo(mob.getPos());
//            return dist >= 7;
        }
        return false;
    }

    @Override
    public void start()
    {
        ticks = 0;
        spawned = 0;
        cooldown = 0;
    }

    @Override
    public boolean shouldContinue()
    {
        return (mob.getTarget() != null) && spawned < maxSpawned;
    }

    @Override
    public boolean canStop()
    {
        return ticks <= 10 || ticks >= 50;
    }

    @Override
    public void stop()
    {
    }


    @Override
    public void tick()
    {
        super.tick();
        ticks += getTickCount(1);
        cooldown -= getTickCount(1);

        World world = mob.getWorld();

        LivingEntity target = mob.getTarget();
        if (ticks > 30 && ticks < 50 && (world.getTime() & 2) == 0)
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

        if (ticks > 30 && spawned < maxSpawned && cooldown <= 0)
        {
            Vec3d toTarget = target.getPos().subtract(mob.getPos());

            var projectile = NMEntities.ACID_SPRAY.create(world);
            float speed = toTarget.length() > 20 ? 2 : 0.5f;
            projectile.target(new Vec3d(mob.getX(), mob.getY() + 4, mob.getZ()),
                    target.getPos(), speed, 1);
            projectile.setOwner(mob);
            world.spawnEntity(projectile);
            mob.playSound(NMSounds.BH_SPIT, 4, 0.9f);

            cooldown = 6;
            spawned++;
        }
    }
}
