package com.neep.neepmeat.mixin;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(method = "render", at  = @At(value = "HEAD"), cancellable = true)
    public void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        var renderer = PLCHudRenderer.getInstance();
        if (renderer != null && renderer.onRender())
        {
            ci.cancel();
        }
    }
}
