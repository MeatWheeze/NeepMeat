package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.gen.AccessorGeneratorFieldGetter;

import java.util.function.Consumer;

public class NMExtraModels implements ExtraModelProvider
{
    public static NMExtraModels EXTRA_MODELS = new NMExtraModels();
//    public static ResourceManager MANAGER;

    public static Identifier BIG_LEVER_HANDLE = new Identifier(NeepMeat.NAMESPACE, "block/big_lever_handle");
    public static Identifier ITEM_PUMP_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_shuttle");
    public static Identifier ITEM_PUMP_CHEST = new Identifier(NeepMeat.NAMESPACE, "block/item_pump_chest");
    public static Identifier VALVE_WHEEL = new Identifier(NeepMeat.NAMESPACE, "block/valve/wheel");
    public static Identifier LO_ARMATURE = new Identifier(NeepMeat.NAMESPACE, "block/linear_oscillator/armature");
    public static Identifier MOTOR_ROTOR = new Identifier(NeepMeat.NAMESPACE, "block/motor_rotor");
    public static Identifier DEPLOYER_SHUTTLE = new Identifier(NeepMeat.NAMESPACE, "block/deployer/shuttle");
    public static Identifier AGITATOR_BLADES = new Identifier(NeepMeat.NAMESPACE, "block/agitator/agitator_blades");
    public static Identifier INTEGRATOR_BASE = new Identifier(NeepMeat.NAMESPACE, "block/integrator/base");

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
        out.accept(INTEGRATOR_BASE);
    }
}
