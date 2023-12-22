package com.neep.meatlib.mixin;

import com.neep.meatlib.item.IMeatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
