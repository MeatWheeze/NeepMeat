package com.neep.neepmeat.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class NMExtraModels implements ExtraModelProvider
{
    public static final PartialModel P_CRUSHER_JAW = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/grinder/jaw"));
    public static final PartialModel P_PYLON_ROTOR = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/pylon/rotor"));
    public static final PartialModel P_PYLON_ACTIVE_ROTOR = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/pylon/rotor_active"));
    public static final PartialModel P_MOTOR_ROTOR = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/motor_rotor"));
    public static final PartialModel P_PLC_ROBOT = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/table_controller/robot"));
    public static final PartialModel LO_ARMATURE = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/linear_oscillator/armature"));
    public static final PartialModel STIRLING_ENGINE_ROTOR = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/stirling_engine/rotor"));
    public static final PartialModel HYDRAULIC_PRESS_ARM = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/hydraulic_press/arm"));
    public static final PartialModel LARGE_MOTOR_ROTOR = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/large_motor/large_motor_rotor.obj"));
    public static final PartialModel FLYWHEEL = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/flywheel/flywheel.obj"));
    public static final PartialModel CHARNEL_PUMP_PLUNGER = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/charnel_pump/charnel_pump_plunger.obj"));
    public static final PartialModel PHAGE_RAY_BASE = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/phage_ray/phage_ray.obj"));
    public static final PartialModel PHAGE_RAY_BARREL = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "block/phage_ray/phage_ray_barrel.obj"));

    public static final PartialModel COW_LIMB = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "entity/parts/cow_limb"));
    public static final PartialModel COW_HEAD = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "entity/parts/cow_head"));
    public static final PartialModel UNKNOWN_LIMB_1 = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "entity/parts/unknown_limb_1"));
    public static final PartialModel UNKNOWN_LIMB_2 = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "entity/parts/unknown_limb_2"));
    public static final PartialModel UNKNOWN_BODY_1 = new PartialModel(new Identifier(NeepMeat.NAMESPACE, "entity/parts/unknown_body_1"));

    public static NMExtraModels EXTRA_MODELS = new NMExtraModels();

    public static Identifier BIG_LEVER_HANDLE = new Identifier(NeepMeat.NAMESPACE, "block/big_lever_handle");
    public static Identifier ITEM_PUMP_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_shuttle");
    public static Identifier ITEM_PUMP_CHEST = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_chest");
    public static Identifier VALVE_WHEEL = new Identifier(NeepMeat.NAMESPACE, "block/valve/wheel");
    public static Identifier MOTOR_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/motor_rotor");
    public static Identifier DEPLOYER_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/deployer/shuttle");
    public static Identifier AGITATOR_BLADES = new Identifier(NeepMeat.NAMESPACE, "block/agitator/agitator_blades");
    public static Identifier MIXER_AGITATOR_BLADES = new Identifier(NeepMeat.NAMESPACE, "block/mixer/agitator");
    public static Identifier INTEGRATOR_BASE = new Identifier(NeepMeat.NAMESPACE, "block/integrator/base");
    public static Identifier ADVANCED_INTEGRATOR_MEAT = new Identifier(NeepMeat.NAMESPACE, "block/advanced_integrator/meat");
    public static Identifier ADVANCED_INTEGRATOR_DANGLIES = new Identifier(NeepMeat.NAMESPACE, "block/advanced_integrator/danglies");
    public static Identifier TROMMEL_MESH = new Identifier(NeepMeat.NAMESPACE, "block/trommel/mesh");
    public static Identifier SMALL_TROMMEL_MESH = new Identifier(NeepMeat.NAMESPACE, "block/small_trommel/mesh");
    public static Identifier LARGE_BLADE = new Identifier(NeepMeat.NAMESPACE, "block/blades/blade");
    public static Identifier PUMP = new Identifier(NeepMeat.NAMESPACE, "block/bottler/pump");
    public static Identifier PYLON_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/pylon/rotor");
    public static Identifier PYLON_ROTOR_ACTIVE = new Identifier(NeepMeat.NAMESPACE, "block/pylon/rotor_active");
    public static Identifier SYNTHESISER_PLUNGER = new Identifier(NeepMeat.NAMESPACE, "block/synthesiser/plunger");
    public static Identifier SURGERY_ROBOT = new Identifier(NeepMeat.NAMESPACE, "block/table_controller/robot");
    public static Identifier EGG = new Identifier(NeepMeat.NAMESPACE, "block/mob_egg");
    public static Identifier CRUSHER_JAW = new Identifier(NeepMeat.NAMESPACE, "block/grinder/jaw");

    public static Identifier ROBOTIC_ARM_SPINNY_BIT = new Identifier(NeepMeat.NAMESPACE, "block/robotic_arm/spinny_bit");
    public static Identifier ROBOTIC_ARM_SEGMENT_1 = new Identifier(NeepMeat.NAMESPACE, "block/robotic_arm/segment_1");
    public static Identifier ROBOTIC_ARM_SEGMENT_2 = new Identifier(NeepMeat.NAMESPACE, "block/robotic_arm/segment_2");


    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out)
    {
        out.accept(BIG_LEVER_HANDLE);
        out.accept(ITEM_PUMP_SHUTTLE);
        out.accept(ITEM_PUMP_CHEST);
        out.accept(VALVE_WHEEL);
        out.accept(MOTOR_ROTOR);
        out.accept(DEPLOYER_SHUTTLE);
        out.accept(AGITATOR_BLADES);
        out.accept(MIXER_AGITATOR_BLADES);
        out.accept(INTEGRATOR_BASE);
        out.accept(ADVANCED_INTEGRATOR_MEAT);
        out.accept(ADVANCED_INTEGRATOR_DANGLIES);
        out.accept(TROMMEL_MESH);
        out.accept(SMALL_TROMMEL_MESH);
        out.accept(LARGE_BLADE);
        out.accept(PUMP);
        out.accept(PYLON_ROTOR);
        out.accept(PYLON_ROTOR_ACTIVE);
        out.accept(SYNTHESISER_PLUNGER);
        out.accept(SURGERY_ROBOT);
        out.accept(EGG);
        out.accept(CRUSHER_JAW);

        out.accept(ROBOTIC_ARM_SPINNY_BIT);
        out.accept(ROBOTIC_ARM_SEGMENT_1);
        out.accept(ROBOTIC_ARM_SEGMENT_2);
    }

    public static void init()
    {

    }
}
