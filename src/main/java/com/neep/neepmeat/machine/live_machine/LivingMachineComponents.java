package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.block.entity.*;
import com.neep.neepmeat.machine.live_machine.component.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LivingMachineComponents
{
    public static final ComponentType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT = register("crusher_segment", new ComponentType.Simple<>());
    public static final ComponentType<LargeTrommelBlockEntity> LARGE_TROMMEL = register("large_trommel", new ComponentType.Simple<>());
    public static final ComponentType<MotorPortBlockEntity> MOTOR_PORT = register("motor_port", new ComponentType.Simple<>());
    public static final ComponentType<IntegrationPortBlockEntity> INTEGRATION_PORT = register("integration_port", new ComponentType.Simple<>());

    public static final ComponentType<HopperComponent> HOPPER = register("hopper", new ComponentType.Simple<>());
    public static final ComponentType<ItemInputComponent> ITEM_INPUT = register("item_input", new ComponentType.Simple<>());
    public static final ComponentType<ItemOutputComponent> ITEM_OUTPUT = register("item_output", new ComponentType.Simple<>());
    public static final ComponentType<FluidInputComponent> FLUID_INPUT = register("fluid_input", new ComponentType.Simple<>());
    public static final ComponentType<FluidOutputComponent> FLUID_OUTPUT = register("fluid_output", new ComponentType.Simple<>());

    public static final ComponentType<LuckyOneBlockEntity> LUCKY_ONE = new ComponentType.Simple<>();

    public static <C extends LivingMachineComponent, T extends ComponentType<C>> T register(String path, T type)
    {
        return register(new Identifier(NeepMeat.NAMESPACE, path), type);
    }

    public static <C extends LivingMachineComponent, T extends ComponentType<C>> T register(Identifier id, T type)
    {
        ComponentType.ID_TO_TYPE.put(type.getBitIdx(), type);
        return Registry.register(ComponentType.REGISTRY, id, type);
    }

    public static void init()
    {
        LivingMachineComponent.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) ->
        {
            if (blockEntity instanceof HopperBlockEntity hopper)
                return new HopperComponent(hopper);
            else
                return null;
        }, Blocks.HOPPER);

//        LivingMachineComponent.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) ->
//        {
//            if (blockEntity instanceof ChestBlockEntity hopper)
//                return new ChestComponent(hopper);
//            else
//                return null;
//
//        }, Blocks.CHEST);
    }
}
