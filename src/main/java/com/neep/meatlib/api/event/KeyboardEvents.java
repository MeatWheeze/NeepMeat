package com.neep.meatlib.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(value= EnvType.CLIENT)
public interface KeyboardEvents
{
    Event<KeyboardEvents> PRE_INPUT = EventFactory.createArrayBacked(
            KeyboardEvents.class,
            (listeners) -> (window, key, scancode, action, modifiers) ->
            {
                for (KeyboardEvents listener : listeners)
                {
                    listener.onKey(window, key, scancode, action, modifiers);
                }
            });

    Event<KeyboardEvents> POST_INPUT = EventFactory.createArrayBacked(
            KeyboardEvents.class,
            (listeners) -> (window, key, scancode, action, modifiers) ->
            {
                for (KeyboardEvents listener : listeners)
                {
                    listener.onKey(window, key, scancode, action, modifiers);
                }
            });


    void onKey(long window, int key, int scancode, int action, int modifiers);
}
