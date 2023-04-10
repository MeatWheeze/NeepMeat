package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
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

    public static boolean primaryHeld;
    public static boolean secondaryHeld;


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
                    int handType = (redirectMainHand ? 1 : 0) + (redirectOffHand ? 1 << 1 : 0);

                    double pitch = Math.toRadians(client.player.getPitch(1));
                    double yaw = Math.toRadians(client.player.getYaw(1));

                    if (client.options.useKey.isPressed())
                    {
                        if (!primaryHeld) sendTrigger(MWAttackC2SPacket.create(MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.PRESS));
                        primaryHeld = true;
                    }
                    else if (primaryHeld)
                    {
                        primaryHeld = false;
                        sendTrigger(MWAttackC2SPacket.create(MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.RELEASE));
                    }

                    if (client.options.attackKey.isPressed())
                    {
                        if (!secondaryHeld) sendTrigger(MWAttackC2SPacket.create(MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.PRESS));
                        secondaryHeld = true;
                    }
                    else if (secondaryHeld)
                    {
                        secondaryHeld = false;
                        sendTrigger(MWAttackC2SPacket.create(MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.RELEASE));
                    }

                    // Suppress the base game's attack processing
                    while (client.options.attackKey.wasPressed());
                }
            }
        });
    }

    public static void sendTrigger(PacketByteBuf buf)
    {
        ClientPlayNetworking.send(MWAttackC2SPacket.ID, buf);
    }
}
