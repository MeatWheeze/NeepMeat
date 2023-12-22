package com.neep.meatlib.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ServerWorld.ServerEntityHandler.class)
public class ServerEntityManagerMixin
{
//    @Inject(method = "startTracking", at = @At("HEAD"))
//    protected void onStartTracking()
//    {
//    }
}
