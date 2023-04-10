package com.neep.meatlib.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(value= EnvType.CLIENT)
public interface InputEvents
{
    Event<InputEvents> PRE_INPUT = EventFactory.createArrayBacked(InputEvents.class,
            (listeners) -> (window, key, scancode, action, modifiers) ->
            {
                for (InputEvents listener : listeners)
                {
                    listener.onKey(window, key, scancode, action, modifiers);
                }
            });

    Event<InputEvents> POST_INPUT = EventFactory.createArrayBacked(InputEvents.class,
            (listeners) -> (window, key, scancode, action, modifiers) ->
            {
                for (InputEvents listener : listeners)
                {
                    listener.onKey(window, key, scancode, action, modifiers);
                }
            });

    void onKey(long window, int key, int scancode, int action, int modifiers);
}
