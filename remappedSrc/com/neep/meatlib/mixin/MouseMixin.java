package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.InputEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(Mouse.class)
public class MouseMixin
{
    @Inject(method = "onMouseButton", at = @At(value = "HEAD"))
    void preMouseButton(long window, int button, int action, int mods, CallbackInfo ci)
    {
        InputEvents.PRE_INPUT.invoker().onKey(window, button, -1, action, mods);
    }
    @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
    void postMouseButton(long window, int button, int action, int mods, CallbackInfo ci)
    {
        InputEvents.POST_INPUT.invoker().onKey(window, button, -1, action, mods);
    }

}
