package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.machine.live_machine.block.entity.MotorPortBlockEntity;
import com.neep.neepmeat.machine.live_machine.component.HopperComponent;
import com.neep.neepmeat.machine.live_machine.component.IItemOutputComponent;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;

public class LivingMachineComponents
{
    public static final ComponentType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT = new ComponentType.Simple<>();
    public static final ComponentType<MotorPortBlockEntity> MOTOR_PORT = new ComponentType.Simple<>();

    public static final ComponentType<HopperComponent> HOPPER = new ComponentType.Simple<>();
    public static final ComponentType<IItemOutputComponent> ITEM_OUTPUT = new ComponentType.Simple<>();

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
