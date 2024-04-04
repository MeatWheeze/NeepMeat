package com.neep.neepmeat.machine.live_machine;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.TestLivingMachineBE;
import com.neep.neepmeat.block.MachineBlock;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.block.*;
import com.neep.neepmeat.machine.live_machine.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import static com.neep.neepmeat.init.NMBlockEntities.register;
import static com.neep.neepmeat.init.NMBlocks.MACHINE_SETTINGS;

public class LivingMachines
{
    public static Block BASE_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("base_machine_block", FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static final Block MOTOR_PORT = BlockRegistry.queue(new MotorPortBlock("motor_port", ItemSettings.block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block INTEGRATION_PORT = BlockRegistry.queue(new IntegrationPortBlock("integration_port", ItemSettings.block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block ITEM_OUTPUT_PORT = BlockRegistry.queue(new ItemOutputPortBlock("item_output_port", ItemSettings.block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final BigBlock<CrusherSegmentBlock.CrusherSegmentStructureBlock> CRUSHER_SEGMENT = BlockRegistry.queue(new CrusherSegmentBlock("crusher_segment", FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block()));
    public static final BigBlock<?> LARGE_TROMMEL = BlockRegistry.queue(new LargeTrommelBlock("large_trommel",FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block()));
    public static final BigBlock<LargestHopperBlock.StructureBlock> LARGEST_HOPPER = BlockRegistry.queue(new LargestHopperBlock("largest_hopper", FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block()));

    public static BlockEntityType<MotorPortBlockEntity> MOTOR_PORT_BE;
    public static BlockEntityType<IntegrationPortBlockEntity> INTEGRATION_PORT_BE;
    public static BlockEntityType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT_BE;
    public static BlockEntityType<LargeTrommelBlockEntity> LARGE_TROMMEL_BE;
    public static BlockEntityType<LargestHopperBlockEntity> LARGEST_HOPPER_BE;

    public static BlockEntityType<TestLivingMachineBE> TEST_LIVING_MACHINE_BE;
    public static BlockEntityType<ItemOutputPortBlockEntity> ITEM_OUTPUT_PORT_BE;

    public static void init()
    {
        MOTOR_PORT_BE = register("motor_port", (p, s) -> new MotorPortBlockEntity(MOTOR_PORT_BE, p, s), MOTOR_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.MOTOR_PORT_BE);
        INTEGRATION_PORT_BE = register("integration_port", (p, s) -> new IntegrationPortBlockEntity(INTEGRATION_PORT_BE, p, s), INTEGRATION_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.INTEGRATION_PORT_BE);
        FluidStorage.SIDED.registerForBlockEntity(IntegrationPortBlockEntity::getFluidStorage, LivingMachines.INTEGRATION_PORT_BE);
        CRUSHER_SEGMENT_BE = register("crusher_segment", (p, s) -> new CrusherSegmentBlockEntity(CRUSHER_SEGMENT_BE, p, s), CRUSHER_SEGMENT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.CRUSHER_SEGMENT_BE);
        LARGE_TROMMEL_BE = register("large_trommel", (p, s) -> new LargeTrommelBlockEntity(LARGE_TROMMEL_BE, p, s), LARGE_TROMMEL);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.LARGE_TROMMEL_BE);
        LARGEST_HOPPER_BE = register("largest_hopper", (p, s) -> new LargestHopperBlockEntity(LARGEST_HOPPER_BE, p, s), LARGEST_HOPPER);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.LARGEST_HOPPER_BE);
        ITEM_OUTPUT_PORT_BE = register("item_output_port", (p, s) -> new ItemOutputPortBlockEntity(ITEM_OUTPUT_PORT_BE, p, s), ITEM_OUTPUT_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.ITEM_OUTPUT_PORT_BE);
        ItemStorage.SIDED.registerForBlockEntity(ItemOutputPortBlockEntity::getStorage, ITEM_OUTPUT_PORT_BE);

        TEST_LIVING_MACHINE_BE = register("test_living_machine", (p, s) -> new TestLivingMachineBE(LivingMachines.TEST_LIVING_MACHINE_BE, p, s), NMBlocks.TEST_LIVING_MACHINE);
    }
}
