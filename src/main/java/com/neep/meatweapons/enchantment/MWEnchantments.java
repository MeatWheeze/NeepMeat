package com.neep.meatweapons.enchantment;

import com.neep.meatlib.enchantment.CustomEnchantment;
import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MWEnchantments
{
    public static Enchantment SPIKES;
    public static Enchantment ARGH;

    public static void init()
    {
        SPIKES = register("spikes", new ThingEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEAPON, 4, EquipmentSlot.MAINHAND));
    }

    private static Enchantment register(String name, Enchantment enchantment)
    {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(MeatWeapons.NAMESPACE, name), enchantment);
    }

    protected static class ThingEnchantment extends Enchantment implements CustomEnchantment
    {
        protected final int maxLevel;

        protected ThingEnchantment(Rarity weight, EnchantmentTarget type, int maxLevel, EquipmentSlot... slotTypes)
        {
            super(weight, type, slotTypes);
            this.maxLevel = maxLevel;
        }

        @Override
        public float getAttackDamage(int level, EntityGroup group)
        {
            return Math.max(0f, level) * 0.5f;
        }

        @Override
        public int getMinPower(int level)
        {
            return (int) Math.max(0, Math.floor(MathHelper.lerp(((float) level) / maxLevel, 0, 30)) - 11);
        }

        @Override
        public int getMaxPower(int level)
        {
            return getMinPower(level) + 11;
        }

        @Override
        public int getMaxLevel()
        {
            return 4;
        }

        @Override
        public boolean isAcceptableItem(ItemStack stack)
        {
            return stack.isOf(MWItems.ASSAULT_DRILL);
        }
    }
}
