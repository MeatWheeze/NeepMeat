package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MWKeys
{
    public static KeyBinding AIRTRUCK_FORWARDS;

    public static KeyBinding registerKeyBinding(String namespace, String id, String category, int def)
    {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + namespace + "." + id,
                InputUtil.Type.KEYSYM,
                def,
                "category." + namespace + "." + category));
    }

    public static void registerKeybinds()
    {
        AIRTRUCK_FORWARDS = registerKeyBinding(MeatWeapons.NAMESPACE, "forwards", "movement", GLFW.GLFW_KEY_W);
//        AIRTRUCK_FORWARDS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "key.meatweapons.forwards",
//                InputUtil.Type.KEYSYM,
//                GLFW.GLFW_KEY_W,
//                "category.meatweapons.movement"));

        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (client.player == null)
                return;

            if (client.player.getRootVehicle() instanceof AbstractVehicleEntity vehicle)
            {
//                while (AIRTRUCK_FORWARDS.wasPressed())
//                {
//                    vehicle.keyForwards(true);
//                }
            }

        });
    }

    static
    {
    }
}
