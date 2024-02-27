package com.neep.meatweapons.entity;

import com.neep.meatweapons.damage.MWDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BulletDamageSource extends DamageSource
{
    private final float punch;
    private final int regenTime;

    public BulletDamageSource(RegistryEntry<DamageType> type, @Nullable Entity attacker, float punch, int regenTime)
    {
        super(type, null, attacker);
        this.punch = punch;
        this.regenTime = regenTime;
    }

//    public BulletDamageSource(LivingEntity attacker, float punch)
//    {
//        this(attacker, punch, 20);
//    }

//    public BulletDamageSource(LivingEntity attacker, float punch, int regenTime)
//    {
//        this.attacker = attacker;
//        this.punch = punch;
//        this.regenTime = regenTime;
//    }

    public static BulletDamageSource create(World world, LivingEntity attacker, float punch)
    {
        return new BulletDamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(MWDamageSources.BULLET), attacker, punch, 15);
    }

    public static BulletDamageSource create(World world, LivingEntity attacker, float punch, int regenTime)
    {
        return new BulletDamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(MWDamageSources.BULLET), attacker, punch, regenTime);
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
