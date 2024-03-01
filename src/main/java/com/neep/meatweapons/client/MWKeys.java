package com.neep.meatweapons.client;

import com.neep.meatlib.api.event.InputEvents;
import com.neep.meatlib.api.event.UseAttackCallback;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.GunItem;
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
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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

        InputEvents.POST_INPUT.register(MWKeys::onKey);

        // Suppress use item and attack events when we are listening for key release events
        UseAttackCallback.DO_USE.register(client -> !primaryHeld);
        UseAttackCallback.DO_ATTACK.register(client -> !secondaryHeld);
    }

    public static void sendTrigger(World world, PlayerEntity player, GunItem main, GunItem off, int trigger, double pitch, double yaw, int handType, MWAttackC2SPacket.ActionType actionType)
    {
        if (main != null)
        {
            if (actionType.pressed())
                main.triggerClient(world, player, player.getStackInHand(Hand.MAIN_HAND), trigger, pitch, yaw, MWAttackC2SPacket.HandType.of(handType));
        }

        if (off != null)
        {
            if (actionType.pressed())
                off.triggerClient(world, player, player.getStackInHand(Hand.OFF_HAND), trigger, pitch, yaw, MWAttackC2SPacket.HandType.of(handType));
        }
        PacketByteBuf buf = MWAttackC2SPacket.create(trigger, pitch, yaw, handType, actionType);
        ClientPlayNetworking.send(MWAttackC2SPacket.ID, buf);
    }

    private static void onKey(long window, int key, int scancode, int action, int modifiers)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        GunItem mainHand = MeatWeapons.redirectClicks(client.player.getMainHandStack());
        GunItem offHand = MeatWeapons.redirectClicks(client.player.getOffHandStack());
        World world = client.world;

        if (mainHand != null || offHand != null)
        {
            int handType = (mainHand != null ? 1 : 0) + (offHand != null ? 1 << 1 : 0);

            Camera camera = client.gameRenderer.getCamera();
            double pitch = Math.toRadians(camera.getPitch());
            double yaw = Math.toRadians(camera.getYaw());

            if (client.options.useKey.isPressed())
            {
                if (!primaryHeld)
                    sendTrigger(world, client.player, mainHand, offHand,
                            MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.PRESS);
                primaryHeld = true;
            }
            else if (primaryHeld)
            {
                primaryHeld = false;
                sendTrigger(world, client.player, mainHand, offHand,
                        MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.RELEASE);
            }

            if (client.options.attackKey.isPressed())
            {
                if (!secondaryHeld)
                    sendTrigger(world, client.player, mainHand, offHand,
                            MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.PRESS);
                secondaryHeld = true;
            }
            else if (secondaryHeld)
            {
                secondaryHeld = false;
                sendTrigger(world, client.player, mainHand, offHand, MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, handType, MWAttackC2SPacket.ActionType.RELEASE);
            }

            // Suppress the base game's input processing.
            while (client.options.attackKey.wasPressed())
            {
            }

            while (client.options.useKey.wasPressed())
            {
            }
        }
        else
        {
            // Release held keys when the special item is no longer held.
            // This should fix switching to a new item without releasing the key.
            primaryHeld = false;
            secondaryHeld = false;
        }
    }
}
