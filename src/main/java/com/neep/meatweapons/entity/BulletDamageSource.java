package com.neep.meatweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import org.jetbrains.annotations.Nullable;

public class BulletDamageSource extends EntityDamageSource
{
    private final Entity attacker;
    private final float punch;
    private final int regenTime;

    public BulletDamageSource(String name, LivingEntity attacker, float punch)
    {
        this(name, attacker, punch, 20);
    }

    public BulletDamageSource(String name, LivingEntity attacker, float punch, int regenTime)
    {
        super(name, attacker);
        this.attacker = attacker;
        this.punch = punch;
        this.regenTime = regenTime;
    }

    public static BulletDamageSource create(LivingEntity attacker, float punch)
    {
        return new BulletDamageSource("bullet", attacker, punch);
    }

    public static BulletDamageSource create(LivingEntity attacker, float punch, int regenTime)
    {
        return new BulletDamageSource("bullet", attacker, punch, regenTime);
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
