package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class NMExtraModels implements ExtraModelProvider
{
    public static NMExtraModels EXTRA_MODELS = new NMExtraModels();

    public static Identifier BIG_LEVER_HANDLE = new Identifier(NeepMeat.NAMESPACE, "block/big_lever_handle");
    public static Identifier ITEM_PUMP_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_shuttle");
    public static Identifier ITEM_PUMP_CHEST = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_chest");
    public static Identifier VALVE_WHEEL = new Identifier(NeepMeat.NAMESPACE, "block/valve/wheel");
    public static Identifier LO_ARMATURE = new Identifier(NeepMeat.NAMESPACE, "block/linear_oscillator/armature");
    public static Identifier MOTOR_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/motor_rotor");
    public static Identifier DEPLOYER_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/deployer/shuttle");
    public static Identifier AGITATOR_BLADES = new Identifier(NeepMeat.NAMESPACE, "block/agitator/agitator_blades");
    public static Identifier MIXER_AGITATOR_BLADES = new Identifier(NeepMeat.NAMESPACE, "block/mixer/agitator");
    public static Identifier INTEGRATOR_BASE = new Identifier(NeepMeat.NAMESPACE, "block/integrator/base");
    public static Identifier STIRLING_ENGINE_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/stirling_engine/rotor");
    public static Identifier TROMMEL_MESH = new Identifier(NeepMeat.NAMESPACE, "block/trommel/mesh");
    public static Identifier SMALL_TROMMEL_MESH = new Identifier(NeepMeat.NAMESPACE, "block/small_trommel/mesh");
    public static Identifier HYDRAULIC_PRESS_ARM = new Identifier(NeepMeat.NAMESPACE, "block/hydraulic_press/arm");
    public static Identifier LARGE_BLADE = new Identifier(NeepMeat.NAMESPACE, "block/blades/blade");
    public static Identifier PUMP = new Identifier(NeepMeat.NAMESPACE, "block/bottler/pump");
    public static Identifier PYLON_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/pylon/rotor");
    public static Identifier SYNTHESISER_PLUNGER = new Identifier(NeepMeat.NAMESPACE, "block/synthesiser/plunger");
    public static Identifier SURGERY_ROBOT = new Identifier(NeepMeat.NAMESPACE, "block/table_controller/robot");
    public static Identifier EGG = new Identifier(NeepMeat.NAMESPACE, "block/mob_egg");
    public static Identifier CRUSHER_JAW = new Identifier(NeepMeat.NAMESPACE, "block/grinder/jaw");

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {
        out.accept(BIG_LEVER_HANDLE);
        out.accept(ITEM_PUMP_SHUTTLE);
        out.accept(ITEM_PUMP_CHEST);
        out.accept(VALVE_WHEEL);
        out.accept(LO_ARMATURE);
        out.accept(MOTOR_ROTOR);
        out.accept(DEPLOYER_SHUTTLE);
        out.accept(AGITATOR_BLADES);
        out.accept(MIXER_AGITATOR_BLADES);
        out.accept(INTEGRATOR_BASE);
        out.accept(STIRLING_ENGINE_ROTOR);
        out.accept(TROMMEL_MESH);
        out.accept(SMALL_TROMMEL_MESH);
        out.accept(HYDRAULIC_PRESS_ARM);
        out.accept(LARGE_BLADE);
        out.accept(PUMP);
        out.accept(PYLON_ROTOR);
        out.accept(SYNTHESISER_PLUNGER);
        out.accept(SURGERY_ROBOT);
        out.accept(EGG);
        out.accept(CRUSHER_JAW);
    }
}
