package com.neep.neepmeat.machine.live_machine;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.StructureProperty;
import com.neep.neepmeat.machine.live_machine.block.entity.TestLivingMachineBE;
import com.neep.neepmeat.block.MachineBlock;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.block.*;
import com.neep.neepmeat.machine.live_machine.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import java.util.Map;

import static com.neep.neepmeat.init.NMBlockEntities.register;
import static com.neep.neepmeat.init.NMBlocks.MACHINE_SETTINGS;
import static com.neep.neepmeat.init.NMBlocks.OPAQUE_MACHINE_SETTINGS;

public class LivingMachines
{
    public static Block MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("machine_block", Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(300f),
                    StructureProperty.MASS, new StructureProperty.Entry(1000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block BASE_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("base_machine_block", Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(300f),
            StructureProperty.MASS, new StructureProperty.Entry(2000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block BLOOD_BUBBLE_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("blood_bubble_machine_block", Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(StructureProperty.Function.ADD, -5),
            StructureProperty.MASS, new StructureProperty.Entry(500f),
            StructureProperty.SELF_REPAIR, new StructureProperty.Entry(StructureProperty.Function.ADD, 0.000001f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block MEAT_STEEL_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("meat_steel_machine_block", Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(500f),
            StructureProperty.MASS, new StructureProperty.Entry(1000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block MEAT_STEEL_MACHINE_BLOCK_2 = BlockRegistry.queue(new MachineBlock("meat_steel_machine_block_2", Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(500f),
            StructureProperty.MASS, new StructureProperty.Entry(1000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));

    public static final Block MOTOR_PORT = BlockRegistry.queue(new PortBlock<>("motor_port", ItemSettings.block(), () ->  LivingMachines.MOTOR_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block INTEGRATION_PORT = BlockRegistry.queue(new PortBlock<>("integration_port", ItemSettings.block(), () -> LivingMachines.INTEGRATION_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block ITEM_OUTPUT_PORT = BlockRegistry.queue(new PortBlock<>("item_output_port", ItemSettings.block(), () -> LivingMachines.ITEM_OUTPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block FLUID_INPUT_PORT = BlockRegistry.queue(new PortBlock<>("fluid_input_port", ItemSettings.block(), () -> LivingMachines.FLUID_INPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block FLUID_OUTPUT_PORT = BlockRegistry.queue(new PortBlock<>("fluid_output_port", ItemSettings.block(), () -> LivingMachines.FLUID_OUTPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
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
    public static BlockEntityType<FluidInputPortBlockEntity> FLUID_INPUT_PORT_BE;
    public static BlockEntityType<FluidOutputPortBlockEntity> FLUID_OUTPUT_PORT_BE;

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
        ItemStorage.SIDED.registerForBlockEntity(LargestHopperBlockEntity::getStorage, LARGEST_HOPPER_BE);
        ITEM_OUTPUT_PORT_BE = register("item_output_port", (p, s) -> new ItemOutputPortBlockEntity(ITEM_OUTPUT_PORT_BE, p, s), ITEM_OUTPUT_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(LivingMachines.ITEM_OUTPUT_PORT_BE);
        ItemStorage.SIDED.registerForBlockEntity(ItemOutputPortBlockEntity::getStorage, ITEM_OUTPUT_PORT_BE);

        FLUID_INPUT_PORT_BE = register("fluid_input_port", (p, s) -> new FluidInputPortBlockEntity(FLUID_INPUT_PORT_BE, p, s), FLUID_INPUT_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(FLUID_INPUT_PORT_BE);
        FluidStorage.SIDED.registerForBlockEntity(FluidInputPortBlockEntity::getStorage, FLUID_INPUT_PORT_BE);

        FLUID_OUTPUT_PORT_BE = register("fluid_output_port", (p, s) -> new FluidOutputPortBlockEntity(FLUID_OUTPUT_PORT_BE, p, s), FLUID_OUTPUT_PORT);
        LivingMachineComponent.LOOKUP.registerSelf(FLUID_OUTPUT_PORT_BE);
        FluidStorage.SIDED.registerForBlockEntity(FluidOutputPortBlockEntity::getStorage, FLUID_OUTPUT_PORT_BE);
        FluidPump.SIDED.registerForBlockEntity(FluidOutputPortBlockEntity::getPump, FLUID_OUTPUT_PORT_BE);

        TEST_LIVING_MACHINE_BE = register("test_living_machine", (p, s) -> new TestLivingMachineBE(LivingMachines.TEST_LIVING_MACHINE_BE, p, s), NMBlocks.TEST_LIVING_MACHINE);

        Processes.init();
    }
}
