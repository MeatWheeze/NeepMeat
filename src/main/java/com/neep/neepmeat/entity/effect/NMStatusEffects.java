package com.neep.neepmeat.entity.effect;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NMStatusEffects
{
    public static StatusEffect ASH_PEPARATION =  new AshPreparationStatusEffect(StatusEffectCategory.NEUTRAL, 0x194D33);

    public static void init()
    {
        ASH_PEPARATION = register(NeepMeat.NAMESPACE, "ash_preparation", ASH_PEPARATION);
    }

    private static StatusEffect register(String namespace, String id, StatusEffect entry)
    {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(namespace, id), entry);
    }
}
