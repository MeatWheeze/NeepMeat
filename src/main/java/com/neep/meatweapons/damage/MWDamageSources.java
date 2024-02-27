package com.neep.meatweapons.damage;

import com.neep.meatweapons.MeatWeapons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MWDamageSources
{
    public static final RegistryKey<DamageType> BULLET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MeatWeapons.NAMESPACE, "bullet"));

    public static RegistryEntry<DamageType> get(World world, RegistryKey<DamageType> key)
    {
        return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key);
    }

    public static DamageSource of(RegistryKey<DamageType> key, World world, @Nullable Entity attacker)
    {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
    }

    public static DamageSource of(RegistryKey<DamageType> key, World world)
    {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
