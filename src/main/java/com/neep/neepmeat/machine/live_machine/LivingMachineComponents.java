package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.block.entity.*;
import com.neep.neepmeat.machine.live_machine.component.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;

public class LivingMachineComponents
{
    public static final ComponentType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT = new ComponentType.Simple<>();
    public static final ComponentType<LargeTrommelBlockEntity> LARGE_TROMMEL = new ComponentType.Simple<>();
    public static final ComponentType<ItemInputComponent> ITEM_INPUT = new ComponentType.Simple<>();
    public static final ComponentType<MotorPortBlockEntity> MOTOR_PORT = new ComponentType.Simple<>();
    public static final ComponentType<IntegrationPortBlockEntity> INTEGRATION_PORT = new ComponentType.Simple<>();

    public static final ComponentType<HopperComponent> HOPPER = new ComponentType.Simple<>();
    public static final ComponentType<ItemOutputComponent> ITEM_OUTPUT = new ComponentType.Simple<>();

    public static final ComponentType<FluidInputComponent> FLUID_INPUT = new ComponentType.Simple<>();
    public static final ComponentType<FluidOutputComponent> FLUID_OUTPUT = new ComponentType.Simple<>();

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
