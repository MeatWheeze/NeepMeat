package com.neep.meatlib.registry;

import net.minecraft.util.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

public class SoundRegistry
{
    protected static final Map<Identifier, SoundEvent> SOUNDS = new LinkedHashMap<>();

    public static SoundEvent registerSound(String namespace, String path)
    {
        Identifier id = new Identifier(namespace, path);
        SoundEvent event = SoundEvent.of(id);
        SOUNDS.put(id, event);
        return event;
    }

    public static void init()
    {
        for (Map.Entry<Identifier, SoundEvent> entry : SOUNDS.entrySet())
        {
            Registry.register(Registry.SOUND_EVENT, entry.getKey(), entry.getValue());
        }
    }
}
