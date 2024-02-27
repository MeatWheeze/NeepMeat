package com.neep.neepmeat.mixin;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(method = "render", at  = @At(value = "HEAD"), cancellable = true)
    public void onRender(DrawContext context, float tickDelta, CallbackInfo ci)
    {
        var renderer = PLCHudRenderer.getInstance();
        if (renderer != null && renderer.onRender())
        {
            ci.cancel();
        }
    }
}
