package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.KeyboardEvents;
import com.neep.meatlib.client.event.ScrollEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(Mouse.class)
public class MouseMixin
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "HEAD"))
    void preMouseButton(long window, int button, int action, int mods, CallbackInfo ci)
    {
        KeyboardEvents.PRE_INPUT.invoker().onKey(window, button, -1, action, mods);
    }

    @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
    void postMouseButton(long window, int button, int action, int mods, CallbackInfo ci)
    {
        KeyboardEvents.POST_INPUT.invoker().onKey(window, button, -1, action, mods);
    }

    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    void preMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci)
    {
        if (window == client.getWindow().getHandle())
        {
            double amount = (client.options.getDiscreteMouseScroll().getValue() ? Math.signum(vertical) : vertical)
                    * client.options.getMouseWheelSensitivity().getValue();

            boolean override = ScrollEvents.PRE_SCROLL.invoker().onScroll(window, amount);

            if (override)
            {
                ci.cancel();
            }
        }
    }
}
