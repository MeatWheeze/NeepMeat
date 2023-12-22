package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import com.neep.meatweapons.item.IGunItem;
import com.neep.meatweapons.network.GunFireC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(value= EnvType.CLIENT)
public class MWKeys
{
    public static KeyBinding AIRTRUCK_DOWN;
    public static KeyBinding WEAPON_SECONDARY;
    public static KeyBinding WEAPON_PRIMARY;

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
//        WEAPON_SECONDARY = registerKeyBinding(MeatWeapons.NAMESPACE, "fire_secondary", "general", GLFW.GLFW_KEY_M);
//        WEAPON_PRIMARY = registerKeyBinding(MeatWeapons.NAMESPACE, "fire_primary", "general", GLFW.GLFW_MOUSE_BUTTON_1);

        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (client.player != null)
            {
                boolean redirectMainHand = MeatWeapons.redirectClicks(client.player.getMainHandStack());
                boolean redirectOffHand = MeatWeapons.redirectClicks(client.player.getOffHandStack());

                if (redirectMainHand || redirectOffHand)
                {
                    int handType = (redirectMainHand ? 1 : 0) + (redirectOffHand ? 0 : 1 << 1);

                    while (client.options.useKey.wasPressed())
                    {
                        ClientPlayNetworking.send(GunFireC2SPacket.ID, GunFireC2SPacket.create(0,
                                Math.toRadians(client.player.getPitch(1)), Math.toRadians(client.player.getYaw(1)), handType));
                    }

                    if (client.options.attackKey.isPressed())
                    {
                        ClientPlayNetworking.send(GunFireC2SPacket.ID, GunFireC2SPacket.create(1,
                                Math.toRadians(client.player.getPitch(1)), Math.toRadians(client.player.getYaw(1)), handType));
                    }

                    // Suppress the base game's attack processing
                    while (client.options.attackKey.wasPressed()) ;
                }
            }
        });
    }
}
