package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import com.neep.meatweapons.network.GunFireC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(value= EnvType.CLIENT)
public class MWKeys
{
    public static KeyBinding AIRTRUCK_DOWN;
    public static KeyBinding WEAPON_SECONDARY;

    public static KeyBinding registerKeyBinding(String namespace, String id, String category, int def)
    {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + namespace + "." + id,
                InputUtil.Type.KEYSYM,
                def,
                "key." + namespace + ".category." + category));
    }

    public static void registerKeybinds()
    {
        AIRTRUCK_DOWN = registerKeyBinding(MeatWeapons.NAMESPACE, "down", "general", GLFW.GLFW_KEY_BACKSLASH);
        WEAPON_SECONDARY = registerKeyBinding(MeatWeapons.NAMESPACE, "fire_secondary", "general", GLFW.GLFW_KEY_M);

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            while (WEAPON_SECONDARY.wasPressed())
            {
                ClientPlayNetworking.send(GunFireC2SPacket.ID, GunFireC2SPacket.create(1));
            }
        });
    }
}
