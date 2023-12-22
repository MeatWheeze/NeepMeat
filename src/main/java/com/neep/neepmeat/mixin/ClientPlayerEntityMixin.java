package com.neep.neepmeat.mixin;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
    @Inject(method = "tickNewAi", at = @At(value = "HEAD"), cancellable = true)
    void onTickNewAi(CallbackInfo ci)
    {
        if (PLCHudRenderer.active())
            ci.cancel();
    }
}
