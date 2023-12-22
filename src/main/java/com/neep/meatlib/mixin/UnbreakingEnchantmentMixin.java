package com.neep.meatlib.mixin;

import net.minecraft.enchantment.UnbreakingEnchantment;
import org.spongepowered.asm.mixin.Mixin;

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
