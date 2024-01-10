package com.neep.neepmeat.init;

import com.neep.meatlib.block.*;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.NMSoundGroups;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.entity.CandleCronenCakeBlock;
import com.neep.neepmeat.block.entity.CronenCakeBlock;
import com.neep.neepmeat.block.machine.AgitatorBlock;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.block.sapling.BloodBubbleTreeGenerator;
import com.neep.neepmeat.block.vat.*;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorBlock;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorStructure;
import com.neep.neepmeat.machine.advanced_motor.AdvancedMotorBlock;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnBlock;
import com.neep.neepmeat.machine.assembler.AssemblerBlock;
import com.neep.neepmeat.machine.bottler.BottlerBlock;
import com.neep.neepmeat.machine.breaker.LinearOscillatorBlock;
import com.neep.neepmeat.machine.casting_basin.CastingBasinBlock;
import com.neep.neepmeat.machine.charnel_compactor.CharnelCompactorBlock;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpBlock;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlock;
import com.neep.neepmeat.machine.crafting_station.WorkstationBlock;
import com.neep.neepmeat.machine.crucible.CrucibleBlock;
import com.neep.neepmeat.machine.crucible.FatCollectorBlock;
import com.neep.neepmeat.machine.death_blades.DeathBladesBlock;
import com.neep.neepmeat.machine.deployer.DeployerBlock;
import com.neep.neepmeat.machine.dumper.DumperBlock;
import com.neep.neepmeat.machine.fluid_exciter.FluidExciterBlock;
import com.neep.neepmeat.machine.fluid_rationer.FluidRationerBlock;
import com.neep.neepmeat.machine.flywheel.FlywheelBlock;
import com.neep.neepmeat.machine.grinder.GrinderBlock;
import com.neep.neepmeat.machine.heater.HeaterBlock;
import com.neep.neepmeat.machine.homogeniser.HomogeniserBlock;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressBlock;
import com.neep.neepmeat.machine.integrator.IntegratorBlock;
import com.neep.neepmeat.machine.item_mincer.ItemMincerBlock;
import com.neep.neepmeat.machine.large_motor.LargeMotorBlock;
import com.neep.neepmeat.machine.mincer.MincerBlock;
import com.neep.neepmeat.machine.mixer.MixerBlock;
import com.neep.neepmeat.machine.motor.MotorBlock;
import com.neep.neepmeat.machine.pedestal.PedestalBlock;
import com.neep.neepmeat.machine.pylon.PylonBlock;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import com.neep.neepmeat.machine.solidity_detector.SolidityDetectorBlock;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlock;
import com.neep.neepmeat.machine.surgery_platform.SurgeryPlatformBlock;
import com.neep.neepmeat.machine.synthesiser.SynthesiserBlock;
import com.neep.neepmeat.machine.transducer.TransducerBlock;
import com.neep.neepmeat.machine.trough.TroughBlock;
import com.neep.neepmeat.machine.upgrade_manager.UpgradeManagerBlock;
import com.neep.neepmeat.machine.well_head.WellHeadBlock;
import com.neep.neepmeat.transport.block.energy_transport.VSCBlock;
import com.neep.neepmeat.transport.block.energy_transport.VascularConduitBlock;
import com.neep.neepmeat.transport.block.item_transport.*;
import com.neep.neepmeat.transport.machine.item.BufferBlock;
import com.neep.neepmeat.transport.machine.item.EjectorBlock;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@SuppressWarnings("unused")
public class NMBlocks
{
    // --- Building Blocks ---
    public static final AbstractBlock.Settings METAL = FabricBlockSettings.of(Material.METAL).hardness(3.0f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings BRASS_BLOCKS = FabricBlockSettings.of(Material.METAL).strength(1.8f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings FLUID_PIPE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings VASCULAR_CONDUIT_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings MACHINE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE).nonOpaque().solidBlock(InventoryDetectorBlock::never);
    public static final AbstractBlock.Settings VAT_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings FLUID_MACHINE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings ITEM_PIPE_SETTINGS = FabricBlockSettings.of(Material.METAL).strength(1.1f).sounds(BlockSoundGroup.METAL);

//    public static Block DUAT_STONE = new BaseBuildingBlock("duat_stone", true, FabricBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_COBBLESTONE = new BaseBuildingBlock("duat_cobblestone", true, FabricBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_GRAVEL = BlockRegistry.queue(new BaseBlock("duat_gravel", block(), FabricBlockSettings.of(Material.STONE).strength(1.0f).sounds(BlockSoundGroup.GRAVEL)));

//    public static Block OBJ_TEST = BlockRegistry.queue(new BigBlock("obj_test", FabricBlockSettings.copyOf(Blocks.STONE)));

    public static BasePaintedBlock SMOOTH_TILE = new BasePaintedBlock("smooth_tile", FabricBlockSettings.of(Material.STONE).hardness(5.0f));

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block POLISHED_MERAL_SMALL_BRICKS = new BaseBuildingBlock("polished_metal_small_bricks", true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static BaseBuildingBlock POLISHED_METAL = new BaseBuildingBlock("polished_metal", true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock("blue_polished_metal", true, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock("rusty_metal", false, FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block WHITE_ROUGH_CONCRETE = new BaseBuildingBlock("white_rough_concrete", false, FabricBlockSettings.of(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block GREY_ROUGH_CONCRETE = new BaseBuildingBlock("grey_rough_concrete", false, FabricBlockSettings.of(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new BaseBuildingBlock("yellow_rough_concrete", false, FabricBlockSettings.of(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock("yellow_tiles", false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK = new BaseBuildingBlock("caution_block", false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block DIRTY_RED_TILES = new BaseBuildingBlock("dirty_red_tiles", false, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block DIRTY_WHITE_TILES = new BaseBuildingBlock("dirty_white_tiles", true, FabricBlockSettings.of(Material.AGGREGATE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block SAND_BRICKS = new BaseBuildingBlock("sandy_bricks", true, FabricBlockSettings.copyOf(Blocks.BRICKS));
    public static Block MEAT_STEEL_BLOCK = new BaseBuildingBlock("meat_steel_block", true, FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK));

    public static Block BLOODY_BRICKS = new BaseBuildingBlock("bloody_bricks", true, FabricBlockSettings.copyOf(Blocks.BRICKS));
    public static Block BLOODY_TILES = new BaseBuildingBlock("bloody_tiles", false, FabricBlockSettings.copyOf(BLOODY_BRICKS));

    public static Block REINFORCED_GLASS = new BaseBuildingBlock("reinforced_glass", false, AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never))
    {
        @Override
        public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction)
{
            if (stateFrom.isOf(this) || stateFrom.isOf((Block) stairs) && stateFrom.get(StairsBlock.FACING).equals(direction.getOpposite()))
            {
                return true;
            }
            return super.isSideInvisible(state, stateFrom, direction);
        }

        @Override
        public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            return VoxelShapes.empty();
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1.0f;
        }

        @Override
        public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
        {
            return true;
        }
    };


//    public static Block FILLED_SCAFFOLD = new BaseBuildingBlock("filled_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE));

    public static MetalScaffoldingBlock SCAFFOLD_PLATFORM = new MetalScaffoldingBlock("rusted_metal_scaffold", block(), FabricBlockSettings.of(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));
    public static MetalScaffoldingBlock BLUE_SCAFFOLD = new MetalScaffoldingBlock("blue_metal_scaffold", block(), FabricBlockSettings.of(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));
    public static MetalScaffoldingBlock YELLOW_SCAFFOLD = new MetalScaffoldingBlock("yellow_metal_scaffold", block(), FabricBlockSettings.of(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));

    public static Block RUSTY_VENT = BlockRegistry.queue(new BaseColumnBlock("rusty_column", block(), FabricBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque()));
    public static Block MESH_PANE = BlockRegistry.queue(new BasePaneBlock("mesh_pane", block(), FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTED_BARS = BlockRegistry.queue(new BasePaneBlock("rusted_bars", block(), FabricBlockSettings.of(Material.METAL).strength(3.5f).sounds(NMSoundGroups.METAL)));
    public static Block RUSTY_PANEL = BlockRegistry.queue(new BaseBlock("rusty_panel", FabricBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static Block RUSTY_GRATE = BlockRegistry.queue(new BaseBlock("rusty_vent", FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));

    // Decorations
    public static Block DIRTY_SINK = BlockRegistry.queue(new BaseBlock("dirty_sink", block(), FabricBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque()));
    public static Block SMALL_SINK = BlockRegistry.queue(new SinkBlock("small_sink", block(), FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static Block TELEVISION_OFF = BlockRegistry.queue(new TelevisionBlock("television_off", block(), FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static Block TELEVISION_STATIC = BlockRegistry.queue(new TelevisionBlock("television_static", block(), FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

//    public static Block SLOPE_TEST = BlockRegistry.queue(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

    public static Block SCAFFOLD_TRAPDOOR = BlockRegistry.queue(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", block(), FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque()));

//    public static Block CAUTION_TAPE = BlockRegistry.queue(new CautionTapeBlock("caution_tape", 64, false, FabricBlockSettings.of(Material.CARPET).strength(1.0f).sounds(BlockSoundGroup.STONE).nonOpaque()));


    // --- Machines
//    public static Block TROMMEL = BlockRegistry.queue(new TrommelBlock("trommel", block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block TROMMEL_STRUCTURE = BlockRegistry.queue(new TrommelBlock.Structure("trommel_top", FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SMALL_TROMMEL = BlockRegistry.queue(new SmallTrommelBlock("small_trommel", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SMALL_TROMMEL_STRUCTURE = BlockRegistry.queue(new SmallTrommelBlock.Structure("small_trommel_structure", FabricBlockSettings.copy(SMALL_TROMMEL)));

//    public static Block CENTRIFUGE = BlockRegistry.queue(new CentrifugeBlock("centrifuge", FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block HEATER = BlockRegistry.queue(new HeaterBlock("heater", block().requiresVascular(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block LINEAR_OSCILLATOR = BlockRegistry.queue(new LinearOscillatorBlock("breaker", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block MOTOR = BlockRegistry.queue(new MotorBlock("motor_unit", block().tooltip(TooltipSupplier.hidden(2)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ADVANCED_MOTOR = BlockRegistry.queue(new AdvancedMotorBlock("advanced_motor", block().tooltip(TooltipSupplier.hidden(2)).requiresVascular(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static LargeMotorBlock LARGE_MOTOR = BlockRegistry.queue(new LargeMotorBlock("large_motor", block().tooltip(TooltipSupplier.hidden(0)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block FLYWHEEL = BlockRegistry.queue(new FlywheelBlock("large_flywheel", block().tooltip(TooltipSupplier.simple(0)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block STIRLING_ENGINE = BlockRegistry.queue(new StirlingEngineBlock("stirling_engine", block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block DEPLOYER = BlockRegistry.queue(new DeployerBlock("deployer", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block AGITATOR = BlockRegistry.queue(new AgitatorBlock("agitator", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block GRINDER = BlockRegistry.queue(new GrinderBlock("grinder", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ALLOY_KILN = BlockRegistry.queue(new AlloyKilnBlock("alloy_kiln", block(), FabricBlockSettings.copyOf(NMBlocks.SAND_BRICKS)));
    public static Block CRUCIBLE = BlockRegistry.queue(new CrucibleBlock("crucible", block(), FLUID_PIPE_SETTINGS));
    public static Block COLLECTOR = BlockRegistry.queue(new FatCollectorBlock("collector", block(), FLUID_PIPE_SETTINGS));
    public static AssemblerBlock ASSEMBLER = BlockRegistry.queue(new AssemblerBlock("assembler", block().requiresVascular(), MACHINE_SETTINGS));

    public static TallBlock FLUID_EXCITER = BlockRegistry.queue(new FluidExciterBlock("fluid_exciter", block(), FabricBlockSettings.of(Material.METAL).sounds(NMSoundGroups.MECHANICAL_MACHINE).hardness(4.0f)));

    public static Block TRANSDUCER = BlockRegistry.queue(new TransducerBlock("transducer", block(), MACHINE_SETTINGS));

    public static Block PEDESTAL = BlockRegistry.queue(new PedestalBlock("pedestal", block().plc(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block CHARNEL_COMPACTOR = BlockRegistry.queue(new CharnelCompactorBlock("charnel_compactor", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));
    public static CharnelPumpBlock CHARNEL_PUMP = BlockRegistry.queue(new CharnelPumpBlock("charnel_pump", block(), FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));
    public static WellHeadBlock WELL_HEAD = BlockRegistry.queue(new WellHeadBlock("well_head", block(), FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));

    public static MixerBlock MIXER = BlockRegistry.queue(new MixerBlock("mixer", block().tooltip(TooltipSupplier.hidden(4)).requiresMotor(), FabricBlockSettings.copyOf(BRASS_BLOCKS)));

    public static Block CASTING_BASIN = BlockRegistry.queue(new CastingBasinBlock("casting_basin", block(), FabricBlockSettings.copyOf(MIXER)));
    public static TallBlock HYDRAULIC_PRESS = BlockRegistry.queue(new HydraulicPressBlock("hydraulic_press", block(), FabricBlockSettings.copyOf(MIXER)));

    public static Block WORKSTATION = BlockRegistry.queue(new WorkstationBlock("workstation", ItemSettings.block().tooltip(TooltipSupplier.hidden(2)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block DEATH_BLADES = BlockRegistry.queue(new DeathBladesBlock("death_blades", block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block FEEDING_TROUGH = BlockRegistry.queue(new TroughBlock("feeding_trough", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(FLUID_MACHINE_SETTINGS)));

//    public static Block SIFTER = BlockRegistry.queue(new SifterBlock("sifter", block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block SIFTER_HOPPER = BlockRegistry.queue(new SifterHopperBlock("hopper", 64, true, FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block PYLON = BlockRegistry.queue(new PylonBlock("pylon", FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SYNTHESISER = BlockRegistry.queue(new SynthesiserBlock("synthesiser", FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block MINCER = BlockRegistry.queue(new MincerBlock("mincer", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ITEM_MINCER = BlockRegistry.queue(new ItemMincerBlock("item_mincer", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block HOMOGENISER = BlockRegistry.queue(new HomogeniserBlock("homogeniser", block().requiresMotor(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block SURGERY_PLATFORM = BlockRegistry.queue(new SurgeryPlatformBlock("surgery_platform", block().plc(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block UPGRADE_MANAGER = BlockRegistry.queue(new UpgradeManagerBlock("upgrade_manager", block(), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block VAT_CASING = BlockRegistry.queue(new VatCasingBlock("vat_casing", block().tooltip(TooltipSupplier.simple(1)), VAT_SETTINGS));
    public static Block VAT_CONTROLLER = BlockRegistry.queue(new VatControllerBlock("vat_controller", block(), VAT_SETTINGS));
    public static Block VAT_ITEM_PORT = BlockRegistry.queue(new ItemPortBlock("vat_item_port", block(), VAT_SETTINGS));
    public static Block VAT_FLUID_PORT = BlockRegistry.queue(new FluidPortBlock("vat_fluid_port", block(), VAT_SETTINGS));
    public static Block VAT_WINDOW = BlockRegistry.queue(new VatWindowBlock("clear_tank_wall", block(), AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never)));

    public static Block FLAME_JET = BlockRegistry.queue(new FlameJetBlock("flame_jet", block().factory(FluidComponentItem::new), MACHINE_SETTINGS));

    public static Block ITEM_BUFFER = BlockRegistry.queue(new DisplayPlatformBlock("item_buffer", block().plc(), MACHINE_SETTINGS));
    //    public static Block SPIGOT = BlockRegistry.queue(new SpigotBlock("spigot", 64, false, FLUID_MACHINE_SETTINGS));

    public static Block BOTTLER = BlockRegistry.queue(new BottlerBlock("bottler", block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), FLUID_MACHINE_SETTINGS));

    public static Block FLUID_RATIONER = BlockRegistry.queue(new FluidRationerBlock("fluid_rationer", block().tooltip(TooltipSupplier.hidden(2)), FabricBlockSettings.copyOf(FLUID_MACHINE_SETTINGS).nonOpaque()));

    // --- Item Transfer ---
    public static Block ITEM_DUCT = BlockRegistry.queue(new ItemDuctBlock("item_duct", block(), FabricBlockSettings.copyOf(Blocks.HOPPER)));
    public static Block PNEUMATIC_TUBE = BlockRegistry.queue(new ItemPipeBlock("item_pipe", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(ITEM_PIPE_SETTINGS)));
    public static Block MERGE_ITEM_PIPE = BlockRegistry.queue(new MergePipeBlock("merge_item_pipe", block(), FabricBlockSettings.copyOf(ITEM_PIPE_SETTINGS)));
    public static Block ITEM_PUMP = BlockRegistry.queue(new ItemPumpBlock("item_pump", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block EJECTOR = BlockRegistry.queue(new EjectorBlock("ejector", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ROUTER = BlockRegistry.queue(new RouterBlock("router", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block BUFFER = BlockRegistry.queue(new BufferBlock("buffer", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copy(Blocks.CHEST)));
    public static Block METAL_BARREL = BlockRegistry.queue(new MetalBarrelBlock("metal_barrel", FabricBlockSettings.copy(RUSTY_METAL_BLOCK)));
    public static Block CONTENT_DETECTOR = BlockRegistry.queue(new InventoryDetectorBlock("content_detector", block().tooltip(TooltipSupplier.simple(2)), FabricBlockSettings.copy(Blocks.OBSERVER)));
    public static Block SOLIDITY_DETECTOR = BlockRegistry.queue(new SolidityDetectorBlock("solidity_detector", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copy(Blocks.OBSERVER)));
    public static Block DUMPER = BlockRegistry.queue(new DumperBlock("dumper", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copy(Blocks.OAK_WOOD)));

    // --- Data ---
    public static Block DATA_CABLE = BlockRegistry.queue(new DataCableBlock("data_cable", block(), VASCULAR_CONDUIT_SETTINGS));
    public static Block VASCULAR_CONDUIT = BlockRegistry.queue(new VascularConduitBlock("vascular_conduit", block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS));
    public static Block POWER_EMITTER = BlockRegistry.queue(new BaseBlock("power_emitter", block().tooltip(TooltipSupplier.simple(1)), VASCULAR_CONDUIT_SETTINGS));
    public static Block VSC = BlockRegistry.queue(new VSCBlock("vsc", block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS));

    // --- Crops ---
    public static Block WHISPER_WHEAT = BlockRegistry.queue(new BaseCropBlock("whisper_wheat", 64, 0, AbstractBlock.Settings.copy(Blocks.WHEAT)));
    public static Block FLESH_POTATO = BlockRegistry.queue(new BaseCropBlock("flesh_potato", "flesh_potato", 64, 2, AbstractBlock.Settings.copy(Blocks.POTATOES)));
//    public static Block ROCKWART = BlockRegistry.queue(new BaseCropBlock("rockwart", 64, true, AbstractBlock.Settings.copy(Blocks.WHEAT)));
    public static Block BLOOD_BUBBLE_SAPLING = BlockRegistry.queue(new BaseSaplingBlock("blood_bubble_sapling", new BloodBubbleTreeGenerator(), block(), FabricBlockSettings.copyOf(Blocks.WARPED_FUNGUS)));

    public static final AbstractBlock.Settings BB_SETTINGS = FabricBlockSettings.of(Material.WOOD).strength(1.1f).sounds(BlockSoundGroup.METAL);
    public static Block BLOOD_BUBBLE_LOG = BlockRegistry.queue(BlockRegistry.createLogBlock("blood_bubble_log", TooltipSupplier.blank()));
    public static Block BLOOD_BUBBLE_WOOD = BlockRegistry.queue(BlockRegistry.createLogBlock("blood_bubble_wood", TooltipSupplier.blank()));
    public static Block BLOOD_BUBBLE_LEAVES= BlockRegistry.queue(BlockRegistry.createLeavesBlock("blood_bubble_leaves", BlockSoundGroup.AZALEA_LEAVES));
    public static Block BLOOD_BUBBLE_LEAVES_FLOWERING = BlockRegistry.queue(BlockRegistry.createLeavesBlock("blood_bubble_leaves_flowering", BlockSoundGroup.SLIME));
    public static Block BLOOD_BUBBLE_PLANKS = new BaseBuildingBlock("blood_bubble_planks", true, FabricBlockSettings.of(Material.WOOD).strength(2.0f).sounds(BlockSoundGroup.WOOD));

    // --- Assembly ---
//    public static Block LINEAR_RAIL = BlockRegistry.queue(new LinearRailBlock("linear_rail", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = BlockRegistry.queue(new BigLeverBlock("big_lever", FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    // --- Integrator ---
    public static Block INTEGRATOR_EGG = BlockRegistry.queue(new IntegratorBlock("integrator_egg", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.SLIME)));

//    public static Block ADVANCED_INTEGRATOR_EGG = BlockRegistry.queue(new AdvancedIntegratorEggBlock("advanced_integrator_egg", block(), FabricBlockSettings.copyOf(INTEGRATOR_EGG)));
    public static BigBlock<AdvancedIntegratorStructure> ADVANCED_INTEGRATOR = BlockRegistry.queue(new AdvancedIntegratorBlock("advanced_integrator", FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = ADVANCED_INTEGRATOR.getStructure();
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = BlockRegistry.queue(new BigBlockStructure(ADVANCED_INTEGRATOR, "advanced_integrator_structure", FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));

    public static Block STATUE = BlockRegistry.queue(new StatueBlock("statue", block(), FabricBlockSettings.copyOf(Blocks.PRISMARINE)));

    public static Block DIRTY_TOILET = BlockRegistry.queue(new ToiletBlock("dirty_toilet", block(), FabricBlockSettings.copyOf(Blocks.STONE)));

    public static Block CRONENCAKE = BlockRegistry.queue(new CronenCakeBlock("cronencake", FabricBlockSettings.copyOf(Blocks.CAKE).sounds(BlockSoundGroup.SLIME)));
    public static Block CANDLE_CRONENCAKE = BlockRegistry.queue(new CandleCronenCakeBlock("candle_cronencake", FabricBlockSettings.copyOf(Blocks.CANDLE_CAKE).sounds(BlockSoundGroup.SLIME)));

//    public static Block ROUTE_TEST = BlockRegistry.queue(new RouteTestBlock("routing_test", FabricBlockSettings.of(Material.METAL)));

    public static Block HOLDING_TRACK = BlockRegistry.queue(new HoldingTrackBlock("holding_track", block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.copyOf(Blocks.RAIL)));

    public static boolean never(BlockState state, BlockView world, BlockPos pos)
    {
        return false;
    }

    private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType)
    {
        return false;
    }

    public static ItemSettings block()
    {
        return ItemSettings.block();
    }
}

