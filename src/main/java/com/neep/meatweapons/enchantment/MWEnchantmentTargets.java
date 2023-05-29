package com.neep.meatweapons.enchantment;

import com.chocohead.mm.api.ClassTinkerers;
import com.neep.meatweapons.MWItems;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class MWEnchantmentTargets
{
    public static EnchantmentTarget ASSAULT_DRILL;

    public static void init()
    {
        ASSAULT_DRILL = ClassTinkerers.getEnum(EnchantmentTarget.class, "ASSAULT_DRILL");
    }

    public static class DrillEnchantmentTarget extends EnchantmentTargetMixin
    {
        @Override
        public boolean isAcceptableItem(Item item)
        {
            return item == MWItems.ASSAULT_DRILL;
        }
    }
}

@Mixin(EnchantmentTarget.class)
abstract class EnchantmentTargetMixin
{
    @Shadow
    abstract boolean isAcceptableItem(Item item);
}
