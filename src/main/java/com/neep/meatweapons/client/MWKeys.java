package com.neep.meatweapons.client;

import com.neep.meatlib.api.event.InputEvents;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
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

        InputEvents.POST_INPUT.register((window, key, scancode, action, modifiers) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            boolean redirectMainHand = MeatWeapons.redirectClicks(client.player.getMainHandStack());
            boolean redirectOffHand = MeatWeapons.redirectClicks(client.player.getOffHandStack());

            if (redirectMainHand || redirectOffHand)
            {
                int handType = (redirectMainHand ? 1 : 0) + (redirectOffHand ? 1 << 1 : 0);

                Camera camera = client.gameRenderer.getCamera();
                double pitch = Math.toRadians(camera.getPitch());
                double yaw = Math.toRadians(camera.getYaw());

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
                while (client.options.attackKey.wasPressed())
                {
                    System.out.println("attak");
                };
            }
        });
    }

    public static void sendTrigger(PacketByteBuf buf)
    {
        ClientPlayNetworking.send(MWAttackC2SPacket.ID, buf);
    }
}
