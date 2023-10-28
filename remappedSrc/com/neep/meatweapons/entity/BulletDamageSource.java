package com.neep.meatweapons.entity;

import com.neep.meatweapons.damage.MWDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

public class BulletDamageSource extends DamageSource
{
    private final Entity attacker;
    private final float punch;
    private final int regenTime;

    public BulletDamageSource(LivingEntity attacker, float punch)
    {
        this(attacker, punch, 20);
    }

    public BulletDamageSource(LivingEntity attacker, float punch, int regenTime)
    {
        super(MWDamageSources.get(attacker.world, MWDamageSources.BULLET), null, attacker);
        this.attacker = attacker;
        this.punch = punch;
        this.regenTime = regenTime;
    }

    public static BulletDamageSource create(LivingEntity attacker, float punch)
    {
        return new BulletDamageSource(attacker, punch);
    }

    public static BulletDamageSource create(LivingEntity attacker, float punch, int regenTime)
    {
        return new BulletDamageSource(attacker, punch, regenTime);
    }

    @Nullable
    public Entity getAttacker()
    {
        return attacker;
    }

    public float getPunch()
    {
        return punch;
    }

    public int getRegenTime()
    {
        return regenTime;
    }
}
