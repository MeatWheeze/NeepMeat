package com.neep.neepmeat.machine.live_machine;

import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.TestLivingMachineBE;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.component.HopperComponent;
import com.neep.neepmeat.machine.live_machine.component.ItemOutputComponent;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;

public class LiveMachines
{
    public static final ComponentType<HopperComponent> HOPPER = new ComponentType.Simple<>();
    public static final ComponentType<ItemOutputComponent> ITEM_OUTPUT = new ComponentType.Simple<>();

    public static BlockEntityType<TestLivingMachineBE> TEST_LIVING_MACHINE_BE;

    public static void init()
    {
        TEST_LIVING_MACHINE_BE = NMBlockEntities.register("test_living_machine", (p, s) -> new TestLivingMachineBE(TEST_LIVING_MACHINE_BE, p, s), NMBlocks.TEST_LIVING_MACHINE);

        LivingMachineComponent.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) ->
        {
            if (blockEntity instanceof HopperBlockEntity hopper)
                return new HopperComponent(hopper);
            else
                return null;
        }, Blocks.HOPPER);

        LivingMachineComponent.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) ->
        {
            if (blockEntity instanceof ChestBlockEntity hopper)
                return new ItemOutputComponent(hopper);
            else
                return null;

        }, Blocks.CHEST);
    }
}
