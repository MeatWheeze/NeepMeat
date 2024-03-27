package com.neep.meatlib.mixin;

import com.neep.meatlib.client.api.event.InputEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(Keyboard.class)
public class KeyboardMixin
{
    @Inject(method = "onKey", at = @At(value = "HEAD"))
    void preOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        InputEvents.PRE_INPUT.invoker().onKey(window, key, scancode, action, modifiers);
    }

    @Inject(method = "onKey", at = @At(value = "TAIL"))
    void postOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        InputEvents.POST_INPUT.invoker().onKey(window, key, scancode, action, modifiers);
    }
}
