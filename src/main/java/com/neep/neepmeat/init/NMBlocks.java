package com.neep.neepmeat.init;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.block.*;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.actuator.LinearRailBlock;
import com.neep.neepmeat.block.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.machine.crucible.AlembicBlock;
import com.neep.neepmeat.machine.crucible.CrucibleBlock;
import com.neep.neepmeat.machine.dumper.DumperBlock;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import com.neep.neepmeat.machine.transducer.TransducerBlock;
import com.neep.neepmeat.transport.block.fluid_transport.*;
import com.neep.neepmeat.block.item_transport.*;
import com.neep.neepmeat.block.machine.*;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnBlock;
import com.neep.neepmeat.machine.grinder.GrinderBlock;
import com.neep.neepmeat.machine.mixer.MixerBlock;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.block.vat.*;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.machine.mixer.MixerTopBlock;
import com.neep.neepmeat.machine.motor.MotorBlock;
import com.neep.neepmeat.machine.multitank.MultiTankBlock;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;

@SuppressWarnings("unused")
public class NMBlocks
{
    public static FabricBlockSettings METAL = FabricBlockSettings.of(Material.METAL).hardness(4.0f).sounds(BlockSoundGroup.NETHERITE);

    public static BasePaintedBlock SMOOTH_TILE = new BasePaintedBlock("smooth_tile", FabricBlockSettings.of(Material.STONE).hardness(5.0f));

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block POLISHED_MERAL_SMALL_BRICKS = new BaseBuildingBlock("polished_metal_small_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(BlockSoundGroup.NETHERITE));
    public static BaseBuildingBlock POLISHED_METAL = new BaseBuildingBlock("polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock("blue_polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock("rusty_metal", 64, false, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block GREY_ROUGH_CONCRETE = new BaseBuildingBlock("grey_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new BaseBuildingBlock("yellow_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock("yellow_tiles", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK = new BaseBuildingBlock("caution_block", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block DIRTY_RED_TILES = new BaseBuildingBlock("dirty_red_tiles", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block SAND_BRICKS = new BaseBuildingBlock("sandy_bricks", 64, true, FabricBlockSettings.copyOf(Blocks.BRICKS));

//    public static Block FILLED_SCAFFOLD = new BaseBuildingBlock("filled_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));

    public static MetalScaffoldingBlock SCAFFOLD_PLATFORM = new MetalScaffoldingBlock("rusted_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(2f).sounds(BlockSoundGroup.NETHERITE));
    public static MetalScaffoldingBlock BLUE_SCAFFOLD = new MetalScaffoldingBlock("blue_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(2f).sounds(BlockSoundGroup.NETHERITE));
    public static MetalScaffoldingBlock YELLOW_SCAFFOLD = new MetalScaffoldingBlock("yellow_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(2f).sounds(BlockSoundGroup.NETHERITE));

    // --- Building Blocks ---
    public static Block RUSTY_VENT = BlockRegistry.queue(new BaseColumnBlock("rusty_vent", 64, false, FabricBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque()));
    public static Block MESH_PANE = BlockRegistry.queue(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTED_BARS = BlockRegistry.queue(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTY_PANEL = BlockRegistry.queue(new BaseBlock("rusty_panel", 64, false, FabricBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static Block RUSTY_GRATE = BlockRegistry.queue(new BaseBlock("rusty_grate", 64, false, FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));
    public static Block LEADED_GLASS = BlockRegistry.queue(new BaseGlassBlock("leaded_glass", 64, false, FabricBlockSettings.copy(Blocks.GLASS).strength(3.5f).sounds(BlockSoundGroup.GLASS).solidBlock(ContentDetectorBlock::never)));

    public static Block SLOPE_TEST = BlockRegistry.queue(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

    public static Block SCAFFOLD_TRAPDOOR = BlockRegistry.queue(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", 64, false, FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));

    public static Block CAUTION_TAPE = BlockRegistry.queue(new CautionTapeBlock("caution_tape", 64, false, FabricBlockSettings.of(Material.CARPET).strength(1.0f).sounds(BlockSoundGroup.STONE).nonOpaque()));

    // --- Fluid Pipes ---
    public static final FabricBlockSettings FLUID_PIPE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(1.8f).sounds(BlockSoundGroup.NETHERITE);
    public static Block PIPE = BlockRegistry.queue(new FluidPipeBlock("pipe", 64, true, FluidComponentItem::new, FLUID_PIPE_SETTINGS));
    public static Block IRON_PIPE = BlockRegistry.queue(new FluidPipeBlock("iron_pipe", 64, true, FluidComponentItem::new, FLUID_PIPE_SETTINGS));
    public static Block COPPER_PIPE = BlockRegistry.queue(new CapillaryFluidPipeBlock("copper_pipe", 64, true, FluidComponentItem::new, FLUID_PIPE_SETTINGS));
    public static Block CHECK_VALVE = BlockRegistry.queue(new CheckValveBlock("check_valve", 64, true, FLUID_PIPE_SETTINGS));
    public static Block STOP_VALVE = BlockRegistry.queue(new StopValveBlock("stop_valve", 64, true, FLUID_PIPE_SETTINGS));

    // --- Machines
    public static final FabricBlockSettings MACHINE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.NETHERITE);
    public static Block TROMMEL = BlockRegistry.queue(new TrommelBlock("trommel", 64, false, MACHINE_SETTINGS));
    public static Block TROMMEL_STRUCTURE = BlockRegistry.queue(new TrommelBlock.Structure("trommel_top", MACHINE_SETTINGS));
    public static Block SMALL_TROMMEL = BlockRegistry.queue(new SmallTrommelBlock("small_trommel", 64, false, MACHINE_SETTINGS));
    public static Block SMALL_TROMMEL_STRUCTURE = BlockRegistry.queue(new SmallTrommelBlock.Structure("small_trommel_strucrure", MACHINE_SETTINGS));

    public static Block HEATER = BlockRegistry.queue(new HeaterBlock("heater", 64, false, MACHINE_SETTINGS));
    public static Block LINEAR_OSCILLATOR = BlockRegistry.queue(new LinearOscillatorBlock("breaker", 64, false, MACHINE_SETTINGS));
    public static Block MOTOR = BlockRegistry.queue(new MotorBlock("motor_unit", 64, false, MACHINE_SETTINGS));
    public static Block STIRLING_ENGINE = BlockRegistry.queue(new StirlingEngineBlock("stirling_engine", 64, false, MACHINE_SETTINGS));
    public static Block DEPLOYER = BlockRegistry.queue(new DeployerBlock("deployer", 64, false, MACHINE_SETTINGS));
    public static Block AGITATOR = BlockRegistry.queue(new AgitatorBlock("agitator", 64, false, MACHINE_SETTINGS));
    public static Block GRINDER = BlockRegistry.queue(new GrinderBlock("grinder", 64, false, MACHINE_SETTINGS));
    public static Block ALLOY_KILN = BlockRegistry.queue(new AlloyKilnBlock("alloy_kiln", 64, false, FabricBlockSettings.copyOf(NMBlocks.SAND_BRICKS)));
    public static Block CRUCIBLE = BlockRegistry.queue(new CrucibleBlock("crucible", 64, false, FLUID_PIPE_SETTINGS));
    public static Block ALEMBIC = BlockRegistry.queue(new AlembicBlock("alembic", 64, false, FLUID_PIPE_SETTINGS));

    public static Block CONVERTER = BlockRegistry.queue(new ConverterBlock("converter", 64, false, FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.NETHERITE).hardness(4.0f)));
    public static Block CONVERTER_BASE = BlockRegistry.queue(new ConverterBlock.Base("converter_base", 64, false, FabricBlockSettings.copy(CONVERTER)));

    public static Block TRANSDUCER = BlockRegistry.queue(new TransducerBlock("transducer", 64, false, FLUID_PIPE_SETTINGS));

    public static Block CHARNEL_COMPACTOR = BlockRegistry.queue(new CharnelCompactorBlock("charnel_compactor", 64, true, FabricBlockSettings.copy(CONVERTER)));

    public static final FabricBlockSettings BRASS_BLOCKS = FabricBlockSettings.of(Material.METAL).strength(1.8f).sounds(BlockSoundGroup.NETHERITE);
    public static Block MIXER = BlockRegistry.queue(new MixerBlock("mixer", 64, true, BRASS_BLOCKS));
    public static Block MIXER_TOP = BlockRegistry.queue(new MixerTopBlock("mixer_top", 64, false, FabricBlockSettings.copyOf(MIXER)));

    public static final FabricBlockSettings VAT_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.NETHERITE);
    public static Block VAT_CASING = BlockRegistry.queue(new VatCasingBlock("vat_casing", 64, false, VAT_SETTINGS));
    public static Block VAT_CONTROLLER = BlockRegistry.queue(new VatControllerBlock("vat_controller", 64, false, VAT_SETTINGS));
    public static Block VAT_ITEM_PORT = BlockRegistry.queue(new ItemPortBlock("vat_item_port", 64, false, VAT_SETTINGS));
    public static Block VAT_FLUID_PORT = BlockRegistry.queue(new FluidPortBlock("vat_fluid_port", 64, false, VAT_SETTINGS));
    public static Block VAT_WINDOW = BlockRegistry.queue(new VatWindowBlock("clear_tank_wall", 64, false, AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never)));

    // --- Fluid Transfer ---
    public static final FabricBlockSettings FLUID_MACHINE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.NETHERITE);
    public static Block PUMP = BlockRegistry.queue(new PumpBlock("pump", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block TANK = BlockRegistry.queue(new TankBlock("basic_tank", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block MULTI_TANK = BlockRegistry.queue(new MultiTankBlock("multi_tank", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block GLASS_TANK = BlockRegistry.queue(new GlassTankBlock("basic_glass_tank", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block FLUID_BUFFER = BlockRegistry.queue(new FluidBufferBlock("fluid_buffer", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block ITEM_BUFFER = BlockRegistry.queue(new ItemBufferBlock("item_buffer", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block FLUID_METER = BlockRegistry.queue(new FluidMeter("fluid_meter", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block PRESSURE_GAUGE = BlockRegistry.queue(new PressureGauge("pressure_gauge", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block FLUID_INTERFACE = BlockRegistry.queue(new FluidInterfaceBlock("fluid_port", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block FLUID_DRAIN = BlockRegistry.queue(new FluidDrainBlock("fluid_drain", 64, true, FLUID_MACHINE_SETTINGS));
    public static Block SPIGOT = BlockRegistry.queue(new SpigotBlock("spigot", 64, false, FLUID_MACHINE_SETTINGS));

    // --- Item Transfer ---
    public static final FabricBlockSettings ITEM_PIPE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(1.1f).sounds(BlockSoundGroup.METAL);
    public static Block ITEM_DUCT = BlockRegistry.queue(new ItemDuctBlock("item_duct", 64, true, FabricBlockSettings.copyOf(Blocks.HOPPER)));
    public static Block PNEUMATIC_TUBE = BlockRegistry.queue(new PneumaticTubeBlock("item_pipe", 64, true, ITEM_PIPE_SETTINGS));
    public static Block MERGE_ITEM_PIPE = BlockRegistry.queue(new MergePipeBlock("merge_item_pipe", 64, true, ITEM_PIPE_SETTINGS));
    public static Block ITEM_PUMP = BlockRegistry.queue(new ItemPumpBlock("item_pump", 64, true, MACHINE_SETTINGS));
    public static Block EJECTOR = BlockRegistry.queue(new EjectorBlock("ejector", 64, true, MACHINE_SETTINGS));
    public static Block ROUTER = BlockRegistry.queue(new RouterBlock("router", 64, true, MACHINE_SETTINGS));
    public static Block BUFFER = BlockRegistry.queue(new BufferBlock("buffer", 64, true, FabricBlockSettings.copy(Blocks.CHEST)));
    public static Block CONTENT_DETECTOR = BlockRegistry.queue(new ContentDetectorBlock("content_detector", 64, true, FabricBlockSettings.copy(Blocks.OBSERVER)));
    public static Block DUMPER = BlockRegistry.queue(new DumperBlock("dumper", 64, true, FabricBlockSettings.copy(Blocks.OAK_WOOD)));

    // --- Data ---
    public static Block DATA_CABLE = BlockRegistry.queue(new DataCableBlock("data_cable", 64, true, FLUID_PIPE_SETTINGS));

    // --- Crops ---
    public static Block WHISPER_WHEAT = BlockRegistry.queue(new BaseCropBlock("whisper_wheat", 64, true, AbstractBlock.Settings.copy(Blocks.WHEAT)));
    public static Block FLESH_POTATO = BlockRegistry.queue(new BaseCropBlock("flesh_potato", "flesh_potato", 64, true, AbstractBlock.Settings.copy(Blocks.POTATOES)));
//    public static Block ROCKWART = BlockRegistry.queue(new BaseCropBlock("rockwart", 64, true, AbstractBlock.Settings.copy(Blocks.WHEAT)));

    // --- Assembly ---
    public static Block LINEAR_RAIL = BlockRegistry.queue(new LinearRailBlock("linear_rail", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = BlockRegistry.queue(new BigLeverBlock("big_lever", FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    // --- Integrator ---
    public static Block INTEGRATOR_EGG = BlockRegistry.queue(new IntegratorBlock("integrator_egg", 64, true, FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.SLIME)));

}
