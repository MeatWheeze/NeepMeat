package com.neep.meatlib.mixin.client;

import com.neep.meatlib.client.event.CrosshairRenderEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    void onDrawCrosshair(DrawContext context, CallbackInfo ci)
    {
        boolean override = CrosshairRenderEvent.EVENT.invoker().drawCrosshair(context);
        if (override)
            ci.cancel();
    }
}
