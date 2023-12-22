package com.neep.neepmeat.init;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.block.*;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.actuator.LinearRailBlock;
import com.neep.neepmeat.block.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.block.fluid_transport.*;
import com.neep.neepmeat.block.item_transport.BufferBlock;
import com.neep.neepmeat.block.item_transport.ItemBufferBlock;
import com.neep.neepmeat.block.item_transport.ItemDuctBlock;
import com.neep.neepmeat.block.item_transport.PneumaticTubeBlock;
import com.neep.neepmeat.block.machine.*;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.item.FluidComponentItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;

@SuppressWarnings("unused")
public class NMBlocks
{
    public static BasePaintedBlock SMOOTH_TILE = new BasePaintedBlock("smooth_tile", FabricBlockSettings.of(Material.STONE).hardness(5.0f));

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block POLISHED_MERAL_SMALL_BRICKS = new BaseBuildingBlock("polished_metal_small_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static BaseBuildingBlock RUSTED_IRON_BLOCK = new BaseBuildingBlock("polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock("blue_polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock("rusty_metal", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block GREY_ROUGH_CONCRETE = new BaseBuildingBlock("grey_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new BaseBuildingBlock("yellow_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock("yellow_tiles", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(5.0f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK = new BaseBuildingBlock("caution_block", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(5.0f).sounds(BlockSoundGroup.STONE));

//    public static Block FILLED_SCAFFOLD = new BaseBuildingBlock("filled_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));

    public static MetalScaffoldingBlock SCAFFOLD_PLATFORM = new MetalScaffoldingBlock("rusted_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE));
    public static MetalScaffoldingBlock BLUE_SCAFFOLD = new MetalScaffoldingBlock("blue_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE));
    public static MetalScaffoldingBlock YELLOW_SCAFFOLD = new MetalScaffoldingBlock("yellow_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE));

    // --- Building Blocks ---
    public static Block RUSTY_VENT = BlockRegistry.queueBlock(new BaseColumnBlock("rusty_vent", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));
    public static Block MESH_PANE = BlockRegistry.queueBlock(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTED_BARS = BlockRegistry.queueBlock(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTY_PANEL = BlockRegistry.queueBlock(new BaseBlock("rusty_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block RUSTY_GRATE = BlockRegistry.queueBlock(new BaseBlock("rusty_grate", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block LEADED_GLASS = BlockRegistry.queueBlock(new BaseGlassBlock("leaded_glass", 64, false, FabricBlockSettings.copy(Blocks.GLASS).strength(5.0f).sounds(BlockSoundGroup.GLASS).solidBlock(ContentDetectorBlock::never)));

    public static Block SLOPE_TEST = BlockRegistry.queueBlock(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

    public static Block SCAFFOLD_TRAPDOOR = BlockRegistry.queueBlock(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));

    // --- General Blocks ---
    public static Block PIPE = BlockRegistry.queueBlock(new FluidPipeBlock("pipe", 64, true, FluidComponentItem::new, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block IRON_PIPE = BlockRegistry.queueBlock(new FluidPipeBlock("iron_pipe", 64, true, FluidComponentItem::new, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block COPPER_PIPE = BlockRegistry.queueBlock(new CapillaryFluidPipeBlock("copper_pipe", 64, true, FluidComponentItem::new, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block CHECK_VALVE = BlockRegistry.queueBlock(new CheckValveBlock("check_valve", 64, true, FabricBlockSettings.copyOf(PIPE)));
    public static Block STOP_VALVE = BlockRegistry.queueBlock(new StopValveBlock("stop_valve", 64, true, FabricBlockSettings.copyOf(PIPE)));

    // --- Machines
    public static Block TROMMEL = BlockRegistry.queueBlock(new TrommelBlock("trommel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block TROMMEL_CENTRE = BlockRegistry.queueBlock(new BaseDummyBlock("trommel_centre", FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block HEATER = BlockRegistry.queueBlock(new HeaterBlock("heater", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block LINEAR_OSCILLATOR = BlockRegistry.queueBlock(new LinearOscillatorBlock("linear_oscillator", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block MOTOR = BlockRegistry.queueBlock(new MotorBlock("motor_unit", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block CONVERTER = BlockRegistry.queueBlock(new ConverterBlock("converter", 64, false, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.NETHERITE)));
    public static Block CONVERTER_BASE = BlockRegistry.queueBlock(new ConverterBlock.Base("converter_base", 64, false, FabricBlockSettings.copy(CONVERTER)));
//    public static Block LARGE_CONVERTER = BlockRegistry.queueBlock(new LargeConverterBlock("large_converter", 64, false, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.NETHERITE)));

    // --- Fluid Transfer ---
    public static Block PUMP = BlockRegistry.queueBlock(new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block TANK = BlockRegistry.queueBlock(new TankBlock("basic_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block GLASS_TANK = BlockRegistry.queueBlock(new GlassTankBlock("basic_glass_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block FLUID_BUFFER = BlockRegistry.queueBlock(new FluidBufferBlock("fluid_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block ITEM_BUFFER = BlockRegistry.queueBlock(new ItemBufferBlock("item_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block FLUID_METER = BlockRegistry.queueBlock(new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block PRESSURE_GAUGE = BlockRegistry.queueBlock(new PressureGauge("pressure_gauge", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block FLUID_PORT = BlockRegistry.queueBlock(new FluidPortBlock("fluid_port", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block FLUID_DRAIN = BlockRegistry.queueBlock(new FluidDrainBlock("fluid_drain", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block SPIGOT = BlockRegistry.queueBlock(new SpigotBlock("spigot", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

    // --- Item Transfer ---
    public static Block ITEM_DUCT = BlockRegistry.queueBlock(new ItemDuctBlock("item_duct", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block PNEUMATIC_TUBE = BlockRegistry.queueBlock(new PneumaticTubeBlock("pneumatic_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block ITEM_PUMP = BlockRegistry.queueBlock(new ItemPumpBlock("item_pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block EJECTOR = BlockRegistry.queueBlock(new EjectorBlock("ejector", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block BUFFER = BlockRegistry.queueBlock(new BufferBlock("buffer", 64, true, FabricBlockSettings.copy(Blocks.CHEST)));
    public static Block CONTENT_DETECTOR = BlockRegistry.queueBlock(new ContentDetectorBlock("content_detector", 64, true, FabricBlockSettings.copy(Blocks.OBSERVER)));

    // --- Assembly ---
    public static Block LINEAR_RAIL = BlockRegistry.queueBlock(new LinearRailBlock("linear_rail", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = BlockRegistry.queueBlock(new BigLeverBlock("big_lever", FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    // --- Integrator ---
    public static Block INTEGRATOR_EGG = BlockRegistry.queueBlock(new IntegratorEggBlock("integrator_egg", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.SLIME)));
    public static Block TANK_WALL = BlockRegistry.queueBlock(new TankWallBlock("clear_tank_wall", 64, false, AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(TankWallBlock::never).solidBlock(TankWallBlock::never).suffocates(TankWallBlock::never).blockVision(TankWallBlock::never)));

}
