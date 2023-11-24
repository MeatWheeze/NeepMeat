package com.neep.neepmeat.mixin;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    void onBobView(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        if (PLCHudRenderer.active())
        {
            ci.cancel();
        }
    }
}
