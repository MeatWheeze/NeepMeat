package com.neep.meatweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import org.jetbrains.annotations.Nullable;

public class BulletDamageSource extends EntityDamageSource
{
    private final Entity attacker;
    private final float punch;

    protected BulletDamageSource(String name, LivingEntity attacker, float punch)
    {
        super(name, attacker);
        this.attacker = attacker;
        this.punch = punch;
    }

    public static BulletDamageSource create(LivingEntity attacker, float punch)
    {
        return new BulletDamageSource("bullet", attacker, punch);
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
}
