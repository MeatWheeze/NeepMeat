package com.neep.neepmeat.entity.keeper;

import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class KeeperHealGoal extends Goal
{
    protected int healTime;
    protected float range;
    protected int ticks;
    protected final KeeperEntity entity;
    protected final int maxTicks = 20;

    public KeeperHealGoal(KeeperEntity entity, float range, int healTime)
    {
        this.entity = entity;
        this.range = range;
        this.healTime = healTime;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        return
                (entity.getAttacker() != null && entity.distanceTo(entity.getAttacker()) > range && entity.shouldHeal())
                || (entity.getAttacker() == null && entity.getTarget() == null && entity.getHealth() < entity.getMaxHealth());
    }

    @Override
    public void start()
    {
        super.start();
        ticks = 0;
    }

    @Override
    public void tick()
    {
        super.tick();
        entity.setVelocity(0, entity.getVelocity().y, 0);
        ++ticks;
    }

    @Override
    public boolean shouldContinue()
    {
        return ticks < healTime;
    }

    @Override
    public void stop()
    {
        super.stop();
        if (ticks >= getTickCount(healTime))
        {
            if (entity.world instanceof ServerWorld serverWorld)
            {
                serverWorld.spawnParticles(ParticleTypes.ENTITY_EFFECT, entity.getX(), entity.getY() + 1, entity.getZ(), 30, 0.4, 1.7, 0.4, 0.1);
                serverWorld.playSound(entity.getX(), entity.getY(), entity.getZ(), NMSounds.COMPOUND_INJECTOR, SoundCategory.HOSTILE, 1f, 1f, true);
                entity.setHealth(MathHelper.clamp(0, entity.getHealth() + 8, entity.getMaxHealth()));
            }
        }
    }
}
