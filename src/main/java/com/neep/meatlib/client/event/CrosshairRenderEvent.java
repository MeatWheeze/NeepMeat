package com.neep.meatlib.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface CrosshairRenderEvent
{
    Event<CrosshairRenderEvent> EVENT = EventFactory.createArrayBacked(CrosshairRenderEvent.class,
            listeners -> (context) ->
            {
                boolean override = false;
                for (var listener : listeners)
                {
                    override |= listener.drawCrosshair(context);
                }
                return override;
            });

    boolean drawCrosshair(DrawContext context);
}
