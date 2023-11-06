package com.neep.meatlib.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin
{
    // Stopgap measure for 1.19.2 backport
//    @Inject(method = "getGroup", at = @At(value = "HEAD"), cancellable = true)
//    void getGroup(CallbackInfoReturnable<ItemGroup> cir)
//    {
//        if (this instanceof IMeatItem meatItem)
//        {
//            cir.setReturnValue(meatItem.getGroupOverride());
//            cir.cancel();
//        }
//    }
}
