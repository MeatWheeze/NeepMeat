package com.neep.neepmeat.machine.live_machine;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.block.multi.TallerBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.live_machine.StructureProperty;
import com.neep.neepmeat.block.MachineBlock;
import com.neep.neepmeat.machine.live_machine.block.*;
import com.neep.neepmeat.machine.live_machine.block.entity.*;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import java.util.Map;

import static com.neep.neepmeat.init.NMBlockEntities.register;
import static com.neep.neepmeat.init.NMBlocks.*;
import static com.neep.neepmeat.machine.live_machine.LivingMachineComponents.tooltip;

public class LivingMachines
{
    public static Block LIVING_MACHINE_CONTROLLER = BlockRegistry.queueWithItem(new TestLivingMachineBlock("living_machine_controller", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)), ItemSettings.block());

    public static Block MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("machine_block", block(), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(300f),
                    StructureProperty.MASS, new StructureProperty.Entry(1000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block BASE_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("base_machine_block", block().tooltip(TooltipSupplier.simple(1)), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(300f),
            StructureProperty.MASS, new StructureProperty.Entry(500f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block BLOOD_BUBBLE_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("blood_bubble_machine_block", block(), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(StructureProperty.Function.ADD, -5),
            StructureProperty.MASS, new StructureProperty.Entry(500f),
            StructureProperty.SELF_REPAIR, new StructureProperty.Entry(StructureProperty.Function.ADD, 1e-6f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block MEAT_STEEL_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("meat_steel_machine_block", block(), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(500f),
            StructureProperty.SELF_REPAIR, new StructureProperty.Entry(StructureProperty.Function.ADD, 0.5e-6f),
            StructureProperty.MASS, new StructureProperty.Entry(1000f)), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block MEAT_STEEL_MACHINE_BLOCK_2 = BlockRegistry.queue(new MachineBlock("meat_steel_machine_block_2", block().tooltip(TooltipSupplier.simple(1)), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(500f),
            StructureProperty.SELF_REPAIR, new StructureProperty.Entry(StructureProperty.Function.ADD, 0.5e-6f),
            StructureProperty.MASS, new StructureProperty.Entry(1000f)
    ), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));
    public static Block SKIN_MACHINE_BLOCK = BlockRegistry.queue(new MachineBlock("skin_machine_block", block(), Map.of(
            StructureProperty.MAX_POWER, new StructureProperty.Entry(500f),
            StructureProperty.SELF_REPAIR, new StructureProperty.Entry(StructureProperty.Function.ADD, 1.5e-6f),
            StructureProperty.MASS, new StructureProperty.Entry(1000f)
    ), FabricBlockSettings.copyOf(OPAQUE_MACHINE_SETTINGS)));

    public static final Block MOTOR_PORT = BlockRegistry.queue(new PortBlock<>("motor_port", ItemSettings.block().tooltip(tooltip(LivingMachineComponents.MOTOR_PORT)),
            () ->  LivingMachines.MOTOR_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block INTEGRATION_PORT = BlockRegistry.queue(new PortBlock<>("integration_port", ItemSettings.block().tooltip(tooltip(LivingMachineComponents.INTEGRATION_PORT)),
            () -> LivingMachines.INTEGRATION_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block ITEM_OUTPUT_PORT = BlockRegistry.queue(new PortBlock<>("item_output_port", ItemSettings.block().tooltip(tooltip(LivingMachineComponents.ITEM_OUTPUT)),
            () -> LivingMachines.ITEM_OUTPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block FLUID_INPUT_PORT = BlockRegistry.queue(new PortBlock<>("fluid_input_port", ItemSettings.block().tooltip(tooltip(LivingMachineComponents.FLUID_INPUT)),
            () -> LivingMachines.FLUID_INPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final Block FLUID_OUTPUT_PORT = BlockRegistry.queue(new PortBlock<>("fluid_output_port", ItemSettings.block().tooltip(tooltip(LivingMachineComponents.FLUID_OUTPUT)),
            () -> LivingMachines.FLUID_OUTPUT_PORT_BE, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static final BigBlock<CrusherSegmentBlock.CrusherSegmentStructureBlock> CRUSHER_SEGMENT = BlockRegistry.queue(new CrusherSegmentBlock("crusher_segment", FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block().tooltip(tooltip(LivingMachineComponents.CRUSHER_SEGMENT))));
    public static final BigBlock<?> LARGE_TROMMEL = BlockRegistry.queue(new LargeTrommelBlock("large_trommel",FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block().tooltip(tooltip(LivingMachineComponents.LARGE_TROMMEL))));
    public static final BigBlock<LargestHopperBlock.StructureBlock> LARGEST_HOPPER = BlockRegistry.queue(new LargestHopperBlock("largest_hopper", FabricBlockSettings.copyOf(MACHINE_SETTINGS), ItemSettings.block().tooltip(tooltip(LivingMachineComponents.ITEM_INPUT))));

    public static final TallerBlock LUCKY_ONE = BlockRegistry.queue(new LuckyOneBlock("lucky_one",
            ItemSettings.block().tooltip(tooltip(LivingMachineComponents.LUCKY_ONE).append(TooltipSupplier.simple(1))),
            MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static BlockEntityType<MotorPortBlockEntity> MOTOR_PORT_BE;
    public static BlockEntityType<IntegrationPortBlockEntity> INTEGRATION_PORT_BE;
    public static BlockEntityType<CrusherSegmentBlockEntity> CRUSHER_SEGMENT_BE;
    public static BlockEntityType<LargeTrommelBlockEntity> LARGE_TROMMEL_BE;
    public static BlockEntityType<LargestHopperBlockEntity> LARGEST_HOPPER_BE;

    public static BlockEntityType<LivingMachineControllerBlockEntity> LIVING_MACHINE_CONTROLLER_BE;
    public static BlockEntityType<ItemOutputPortBlockEntity> ITEM_OUTPUT_PORT_BE;
    public static BlockEntityType<FluidInputPortBlockEntity> FLUID_INPUT_PORT_BE;
    public static BlockEntityType<FluidOutputPortBlockEntity> FLUID_OUTPUT_PORT_BE;

    public static BlockEntityType<LuckyOneBlockEntity> LUCKY_ONE_BE;


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

        LIVING_MACHINE_CONTROLLER_BE = register("living_machine_controller", (p, s) -> new LivingMachineControllerBlockEntity(LivingMachines.LIVING_MACHINE_CONTROLLER_BE, p, s), LIVING_MACHINE_CONTROLLER);

        LUCKY_ONE_BE = register("lucky_one", (p, s) -> new LuckyOneBlockEntity(LivingMachines.LUCKY_ONE_BE, p, s), LUCKY_ONE);
        LivingMachineComponent.LOOKUP.registerSelf(LUCKY_ONE_BE);
        BloodAcceptor.SIDED.registerForBlockEntity(LuckyOneBlockEntity::getAcceptor, LUCKY_ONE_BE);

        Processes.init();
    }
}
