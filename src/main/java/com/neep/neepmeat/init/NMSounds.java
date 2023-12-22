package com.neep.neepmeat.init;

import com.neep.meatlib.registry.SoundRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class NMSounds
{
    public static SoundEvent BIG_LEVER_ON = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "big_lever_on");
    public static SoundEvent BIG_LEVER_OFF = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "big_lever_off");
    public static SoundEvent CLICK = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "click");

    public static SoundEvent FUSION_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_fire");
    public static SoundEvent FUSION_HIT = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_hit");
    public static SoundEvent RELOAD = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_reload");

    public static SoundEvent HAND_CANNON_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "hand_cannon_fire");
    public static SoundEvent HAND_CANNON_HIT = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_hit");
    public static SoundEvent HAND_CANNON_RELOAD = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_reload");

    public static SoundEvent AIRTRUCK_STARTING = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "airtruck_starting");
    public static SoundEvent AIRTRUCK_RUNNING = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "airtruck_running");

    public static SoundEvent COMPOUND_INJECTOR = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "compound_injector");

    public static SoundEvent CAKE_SCREAM = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "cake_scream");
    public static SoundEvent CAKE_FIRE = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "cake_fire");

    public static SoundEvent COSMIC_BEAM = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "cosmic_beam");
    public static SoundEvent PYLON_START = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "pylon_start");
    public static SoundEvent BEEP = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "beep");

    public static SoundEvent LMG_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "light_machine_gun_fire");
    public static SoundEvent AR_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "ar_fire");
    public static SoundEvent GRENADE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "grenade_fire");

}
