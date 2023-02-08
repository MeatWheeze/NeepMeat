package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.network.ToolTransformPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import static com.neep.meatweapons.client.MWKeys.registerKeyBinding;

@Environment(value= EnvType.CLIENT)
public class NMKeys
{
    public static KeyBinding TOOL_TRANSFORM;

    public static void registerKeybindings()
    {
        TOOL_TRANSFORM = registerKeyBinding(NeepMeat.NAMESPACE, "transform", "neepmeat", GLFW.GLFW_KEY_C);

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            while (TOOL_TRANSFORM.wasPressed())
            {
                ToolTransformPacket.Client.send();
            }
        });
    }
}
