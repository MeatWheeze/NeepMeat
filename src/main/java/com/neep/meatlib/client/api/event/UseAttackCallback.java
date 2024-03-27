package com.neep.meatlib.client.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

@Environment(value= EnvType.CLIENT)
public interface UseAttackCallback
{
    Event<UseAttackCallback> DO_USE = EventFactory.createArrayBacked(UseAttackCallback.class,
            (listeners) -> (client) ->
            {
                for (UseAttackCallback listener : listeners)
                {
                    boolean result = listener.context(client);
                    if (!result) return false;
                }
                return true;
            });

    Event<UseAttackCallback> DO_ATTACK = EventFactory.createArrayBacked(UseAttackCallback.class,
            (listeners) -> (client) ->
            {
                for (UseAttackCallback listener : listeners)
                {
                    boolean result = listener.context(client);
                    if (!result) return false;
                }
                return true;
            });


    boolean context(MinecraftClient client);
}
