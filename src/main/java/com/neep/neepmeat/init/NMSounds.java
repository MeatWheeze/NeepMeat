package com.neep.neepmeat.init;

import com.neep.meatlib.registry.SoundRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.sound.SoundEvent;

public class NMSounds
{
    public static SoundEvent BIG_LEVER_ON = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "big_lever_on");
    public static SoundEvent BIG_LEVER_OFF = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "big_lever_off");
    public static SoundEvent CLICK = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "click");
    public static SoundEvent ERROR = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "error");

    public static SoundEvent GLOME_HIT = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "glome_hit");
    public static SoundEvent HOUND_HIT = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "hound_hit");
    public static SoundEvent HOUND_DEATH = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "hound_death");

    public static SoundEvent BH_HIT = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "bovine_horror_hit");
    public static SoundEvent BH_CHARGE = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "bovine_horror_charge");
    public static SoundEvent BH_PHASE2 = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "bovine_horror_phase2");
    public static SoundEvent BH_SPIT = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "bovine_horror_spit");

    public static SoundEvent MECHANICAL_MACHINE_PLACE = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "mechanical_machine_place");
    public static SoundEvent MECHANICAL_MACHINE_BREAK = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "mechanical_machine_break");
    public static SoundEvent FLESH_MACHINE_PLACE = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "flesh_machine_place");

    public static SoundEvent PLC_SELECT = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "plc_select");
    public static SoundEvent PLC_SELECT_BLOCK = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "plc_select_block");
    public static SoundEvent IMPLANT_INSTRUCTION_APPLY = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "implant_instruction_apply");
    public static SoundEvent INJECT_INSTRUCTION_APPLY = SoundRegistry.registerSound(NeepMeat.NAMESPACE, "inject");

    public static SoundEvent FUSION_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_fire");
    public static SoundEvent FUSION_BLAST_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_blast_fire");
    public static SoundEvent FUSION_BLAST_CHARGE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_blast_charge");

    public static SoundEvent RELOAD = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_reload");
    public static SoundEvent ZAP_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "zap_fire");
    public static SoundEvent ZAP_HIT = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "zap_hit");


    public static SoundEvent HAND_CANNON_FIRE = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "hand_cannon_fire");
    public static SoundEvent HAND_CANNON_HIT = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_hit");
    public static SoundEvent HAND_CANNON_RELOAD = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "fusion_reload");

    public static SoundEvent DRILL_RUNNING = SoundRegistry.registerSound(MeatWeapons.NAMESPACE, "drill_running");

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
