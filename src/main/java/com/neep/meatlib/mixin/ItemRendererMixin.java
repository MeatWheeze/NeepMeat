package com.neep.meatlib.mixin;

import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin
{
//    @Inject(at = @At(value = "TAIL"), method = "")
//    @Inject(at = @At(value = "TAIL"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
//    private void onItemGuiRender(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci)
//    {
//        RenderItemGuiEvent.EVENT.invoker().interact(renderer, stack, x, y, countLabel);
//    }
}
