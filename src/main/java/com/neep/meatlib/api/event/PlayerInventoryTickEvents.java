package com.neep.meatlib.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerInventoryTickEvents
{
    Event<PlayerInventoryTickEvents> EVENT = EventFactory.createArrayBacked(PlayerInventoryTickEvents.class,
            (listeners) -> (player, worldTime) ->
            {
                for (PlayerInventoryTickEvents listener : listeners)
                {
                    listener.tick(player, worldTime);
                }
            });

    void tick(PlayerEntity player, long time);
}
