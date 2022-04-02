package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class SoundInitialiser
{

    public static Map<Identifier, SoundEvent> SOUNDS = new LinkedHashMap<>();

    public static SoundEvent BIG_LEVER_ON = registerSound("big_lever_on");
    public static SoundEvent BIG_LEVER_OFF = registerSound("big_lever_off");

    public static SoundEvent registerSound(String path)
    {
        Identifier id = new Identifier(NeepMeat.NAMESPACE, path);
        SoundEvent event = new SoundEvent(id);
        SOUNDS.put(id, event);
        return event;
    }

    public static void registerSounds()
    {
        for (Map.Entry<Identifier, SoundEvent> entry : SOUNDS.entrySet())
        {
            Registry.register(Registry.SOUND_EVENT, entry.getKey(), entry.getValue());
        }
    }
}
