package com.neep.neepmeat.mixin;

import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin
{
    @Inject(method = "update", at = @At(value = "TAIL"))
    void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
    {
        var renderer = PLCHudRenderer.getInstance();
        if (renderer != null)
        {
            renderer.onCameraUpdate((Camera) (Object) this, tickDelta);
        }
    }
}
