package com.neep.neepmeat.init;

import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class SoundInitialiser
{

    public static Map<Identifier, SoundEvent> SOUNDS = new LinkedHashMap<>();

    public static SoundEvent BIG_LEVER_ON = registerSound(NeepMeat.NAMESPACE, "big_lever_on");
    public static SoundEvent BIG_LEVER_OFF = registerSound(NeepMeat.NAMESPACE, "big_lever_off");
    public static SoundEvent CLICK = registerSound(NeepMeat.NAMESPACE, "click");

    public static SoundEvent FUSION_FIRE = registerSound(MeatWeapons.NAMESPACE, "fusion_fire");
    public static SoundEvent FUSION_HIT = registerSound(MeatWeapons.NAMESPACE, "fusion_hit");
    public static SoundEvent RELOAD = registerSound(MeatWeapons.NAMESPACE, "fusion_reload");

    public static SoundEvent HAND_CANNON_FIRE = registerSound(MeatWeapons.NAMESPACE, "hand_cannon_fire");
    public static SoundEvent HAND_CANNON_HIT = registerSound(MeatWeapons.NAMESPACE, "fusion_hit");
    public static SoundEvent HAND_CANNON_RELOAD = registerSound(MeatWeapons.NAMESPACE, "fusion_reload");

    public static SoundEvent LMG_FIRE = registerSound(MeatWeapons.NAMESPACE, "light_machine_gun_fire");

    public static SoundEvent registerSound(String namespace, String path)
    {
        Identifier id = new Identifier(namespace, path);
        SoundEvent event = new SoundEvent(id);
        SOUNDS.put(id, event);
        return event;
    }

    public static void initialise()
    {
        for (Map.Entry<Identifier, SoundEvent> entry : SOUNDS.entrySet())
        {
            Registry.register(Registry.SOUND_EVENT, entry.getKey(), entry.getValue());
        }
    }
}
