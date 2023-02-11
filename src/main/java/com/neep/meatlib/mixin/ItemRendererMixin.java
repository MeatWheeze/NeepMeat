package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.RenderItemGuiCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin
{
    @Inject(at = @At(value = "TAIL"),  method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    private void onItemGuiRender(final TextRenderer textRenderer, final ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci)
    {
        RenderItemGuiCallback.EVENT.invoker().interact(textRenderer, stack, x, y, countLabel);

    }

}
