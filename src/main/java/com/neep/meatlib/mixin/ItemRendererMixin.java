package com.neep.meatlib.mixin;

import com.neep.meatlib.client.api.event.RenderItemGuiEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class ItemRendererMixin
{
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "drawItem(Lnet/minecraft/item/ItemStack;II)V")
    private void onItemGuiRender(ItemStack stack, int x, int y, CallbackInfo ci)
    {
        RenderItemGuiEvent.EVENT.invoker().render((DrawContext) (Object) this, client.textRenderer, stack, x, y);
    }
}
