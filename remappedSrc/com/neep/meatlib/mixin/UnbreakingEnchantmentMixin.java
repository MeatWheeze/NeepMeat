package com.neep.meatlib.mixin;

import com.neep.meatlib.item.PoweredItem;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnbreakingEnchantment.class)
public class UnbreakingEnchantmentMixin
{
//    @Inject(method = "isAcceptableItem", at = @At(value = "HEAD"), cancellable = true)
//    public void inject(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
//    {
//        System.out.println("thingy!");
//        if (stack.getItem() instanceof PoweredItem poweredItem && !poweredItem.isDamageable())
//        {
//            cir.setReturnValue(false);
//            cir.cancel();
//        }
//    }
}
