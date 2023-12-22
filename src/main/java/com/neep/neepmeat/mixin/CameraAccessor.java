package com.neep.neepmeat.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor
{
    @Invoker void callSetPos(double x, double y, double z);
    @Invoker void callSetRotation(float yaw, float pitch);
}
