package com.neep.meatlib.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public interface CrosshairRenderEvent
{
    Event<CrosshairRenderEvent> EVENT = EventFactory.createArrayBacked(CrosshairRenderEvent.class,
            listeners -> (context, player) ->
            {
                boolean override = false;
                for (var listener : listeners)
                {
                    override |= listener.drawCrosshair(context, player);
                }
                return override;
            });

    boolean drawCrosshair(DrawContext context, PlayerEntity player);
}
