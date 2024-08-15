package com.neep.meatlib.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ScrollEvents
{
    Event<ScrollEvents> PRE_SCROLL = EventFactory.createArrayBacked( ScrollEvents.class,
            (listeners) -> (window, amount) ->
            {
                boolean override = false;
                for (ScrollEvents listener : listeners)
                {
                    override |= listener.onScroll(window, amount);
                }
                return override;
            });

    boolean onScroll(long window, double amount);
}
