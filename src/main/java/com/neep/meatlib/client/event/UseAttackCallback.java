package com.neep.meatlib.client.event;

import com.neep.meatlib.mixin.client.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

@Environment(value= EnvType.CLIENT)
public interface UseAttackCallback
{
    Event<UseAttackCallback> DO_USE = EventFactory.createArrayBacked(UseAttackCallback.class,
            (listeners) -> (client, player) ->
            {
                for (UseAttackCallback listener : listeners)
                {
                    boolean result = listener.context(client, player);
                    if (!result)
                        return false;
                }
                return true;
            });

    Event<UseAttackCallback> DO_ATTACK = EventFactory.createArrayBacked(UseAttackCallback.class,
            (listeners) -> (client, player) ->
            {
                for (UseAttackCallback listener : listeners)
                {
                    boolean result = listener.context(client, player);
                    if (!result) return false;
                }
                return true;
            });

    static void swingHand(MinecraftClient client, Hand hand)
    {
        client.player.swingHand(hand);
        ((MinecraftClientAccessor) client).setUseItemCooldown(4);
    }

    boolean context(MinecraftClient client, ClientPlayerEntity player);
}
