package com.neep.meatlib.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class MWHudThings
{
    public static void init()
    {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
//            if (
        });
    }
}
