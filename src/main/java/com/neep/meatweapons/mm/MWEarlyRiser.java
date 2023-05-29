package com.neep.meatweapons.mm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class MWEarlyRiser implements Runnable
{

    @Override
    public void run()
    {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ASSAULT_DRILL", "com.neep.meatweapons.enchantment.MWEnchantmentTargets$DrillEnchantmentTarget").build();
//        System.out.println(MWEnchantmentTargets.DrillEnchantmentTarget.class.getName());
//        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ASSAULT_DRILL", MWEnchantmentTargets.DrillEnchantmentTarget.class.getName()).build();
    }
}