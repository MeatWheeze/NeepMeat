package com.neep.meatlib.mixin;

import com.google.common.collect.Lists;
import com.neep.meatlib.enchantment.CustomEnchantment;
import com.neep.meatlib.item.CustomEnchantable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
    @Inject(method = "getPossibleEntries", at  = @At(value = "HEAD"), cancellable = true)
    private static void getEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir)
    {
        // Diverge only if the item implements CustomEnchantable. Vanilla items will be untouched.
        Item item = stack.getItem();
        if (item instanceof CustomEnchantable)
        {
            ArrayList<EnchantmentLevelEntry> list = Lists.newArrayList();

            boolean isBook = stack.isOf(Items.BOOK);
            mainBlock: for (Enchantment enchantment : Registry.ENCHANTMENT)
            {
                boolean allowed = false;
                if (enchantment instanceof CustomEnchantment)
                {
                    // Everything is the same as normal except this ignores the EnchantmentTarget.
                    allowed = enchantment.isAcceptableItem(stack);
                }
                else
                {
                    // Vanilla behaviour for normal enchantments.
                    allowed = enchantment.type.isAcceptableItem(item);
                }

                if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !allowed && !isBook) continue;

                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i)
                {
                    if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                    list.add(new EnchantmentLevelEntry(enchantment, i));
                    continue mainBlock;
                }
            }
            cir.setReturnValue(list);
        }
    }
}
