package com.neep.neepmeat.potion;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.mixin.BrewingRecipeRegistryAccessor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NMPotions
{
    public static final Potion ASH_PREPARATION = register(NeepMeat.NAMESPACE, "ash_preparation", new Potion(new StatusEffectInstance(NMStatusEffects.ASH_PEPARATION, 3600)));

    public static void init()
    {
        BrewingRecipeRegistryAccessor.callRegisterPotionRecipe(Potions.AWKWARD, NMItems.ENLIGHTENED_BRAIN, ASH_PREPARATION);
    }

    private static Potion register(String namespace, String name, Potion potion)
    {
        return Registry.register(Registries.POTION, new Identifier(namespace, name), potion);
    }
}
