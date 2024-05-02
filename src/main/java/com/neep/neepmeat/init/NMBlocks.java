package com.neep.neepmeat.init;

import com.neep.meatlib.block.*;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.NMSoundGroups;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.multiblock2.Multiblock2ControllerBlock;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.entity.BaseDoorBlock;
import com.neep.neepmeat.block.entity.CandleCronenCakeBlock;
import com.neep.neepmeat.block.entity.CronenCakeBlock;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.block.sapling.BloodBubbleTreeGenerator;
import com.neep.neepmeat.block.vat.VatWindowBlock;
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
import com.neep.neepmeat.machine.charnel_pump.ContaminatedDirtBlock;
import com.neep.neepmeat.machine.charnel_pump.WrithingEarthSpoutBlock;
import com.neep.neepmeat.machine.charnel_pump.WrithingStoneBlock;
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
import com.neep.neepmeat.machine.large_crusher.LargeCrusherBlock;
import com.neep.neepmeat.machine.large_crusher.LargeCrusherStructureBlock;
import com.neep.neepmeat.machine.large_motor.LargeMotorBlock;
import com.neep.neepmeat.machine.live_machine.block.TestLivingMachineBlock;
import com.neep.neepmeat.machine.mincer.MincerBlock;
import com.neep.neepmeat.machine.mixer.MixerBlock;
import com.neep.neepmeat.machine.motor.MotorBlock;
import com.neep.neepmeat.machine.pedestal.PedestalBlock;
import com.neep.neepmeat.machine.phage_ray.PhageRayBlock;
import com.neep.neepmeat.machine.power_flower.PowerFlowerControllerBlock;
import com.neep.neepmeat.machine.power_flower.PowerFlowerFluidPortBlock;
import com.neep.neepmeat.machine.power_flower.PowerFlowerGrowthBlock;
import com.neep.neepmeat.machine.power_flower.PowerFlowerSeedsBlock;
import com.neep.neepmeat.machine.pylon.PylonBlock;
import com.neep.neepmeat.machine.separator.SeparatorBlock;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import com.neep.neepmeat.machine.solidity_detector.SolidityDetectorBlock;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineBlock;
import com.neep.neepmeat.machine.surgery_platform.SurgeryPlatformBlock;
import com.neep.neepmeat.machine.synthesiser.SynthesiserBlock;
import com.neep.neepmeat.machine.transducer.TransducerBlock;
import com.neep.neepmeat.machine.trough.TroughBlock;
import com.neep.neepmeat.machine.upgrade_manager.UpgradeManagerBlock;
import com.neep.neepmeat.machine.well_head.WellHeadBlock;
import com.neep.neepmeat.transport.block.energy_transport.EncasedVascularConduitBlock;
import com.neep.neepmeat.transport.block.energy_transport.VSCBlock;
import com.neep.neepmeat.transport.block.energy_transport.VascularConduitBlock;
import com.neep.neepmeat.transport.block.item_transport.*;
import com.neep.neepmeat.transport.machine.item.BufferBlock;
import com.neep.neepmeat.transport.machine.item.EjectorBlock;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlock;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Set;

import static net.minecraft.tag.BlockTags.*;

@SuppressWarnings("unused")
public class NMBlocks
{
    // --- Building Blocks ---
    public static final AbstractBlock.Settings METAL = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).hardness(3.0f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings BRASS_BLOCKS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(1.8f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings FLUID_PIPE_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings VASCULAR_CONDUIT_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings MACHINE_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(3.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE).nonOpaque().solidBlock(InventoryDetectorBlock::never);
    public static final AbstractBlock.Settings OPAQUE_MACHINE_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(3.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE).solidBlock(InventoryDetectorBlock::never);
//    public static final AbstractBlock.Settings VAT_SETTINGS = MeatlibBlockSettings.of(Material.METAL).strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings FLUID_MACHINE_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings ITEM_PIPE_SETTINGS = MeatlibBlockSettings.create(Material.METAL, PICKAXE_MINEABLE).strength(1.1f).sounds(BlockSoundGroup.METAL);

//    public static Block DUAT_STONE = new BaseBuildingBlock("duat_stone", true, MeatlibBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_COBBLESTONE = new BaseBuildingBlock("duat_cobblestone", true, MeatlibBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_GRAVEL = BlockRegistry.queue(new BaseBlock("duat_gravel", block(), MeatlibBlockSettings.of(Material.STONE).strength(1.0f).sounds(BlockSoundGroup.GRAVEL)));

//    public static Block OBJ_TEST = BlockRegistry.queue(new BigBlock("obj_test", MeatlibBlockSettings.copyOf(Blocks.STONE)));

//    public static Block TEST_MULTIBLOCK = BlockRegistry.queue(new TestMultiblock("test_multiblock", MeatlibBlockSettings.create()));

    public static PaintedBlockManager<?> SMOOTH_TILE = new PaintedBlockManager<>("smooth_tile", SmoothTileBlock::new ,MeatlibBlockSettings.create(Material.STONE).hardness(3.0f));

    public static Block TEST_LIVING_MACHINE = BlockRegistry.queueWithItem(new TestLivingMachineBlock("test_living_machine", FabricBlockSettings.copyOf(MACHINE_SETTINGS)), ItemSettings.block());

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", true, MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block POLISHED_MERAL_SMALL_BRICKS = new BaseBuildingBlock("polished_metal_small_bricks", true, MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static BaseBuildingBlock POLISHED_METAL = new BaseBuildingBlock("polished_metal", true, MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock("blue_polished_metal", true, MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock("rusty_metal", false, MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block RUSTY_METAL_DOOR = new BaseDoorBlock("rusty_metal_door", MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque(), block());
    public static Block WHITE_ROUGH_CONCRETE = new RoughConcreteBlock("white_rough_concrete", false, DyeColor.WHITE, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block GREY_ROUGH_CONCRETE = new RoughConcreteBlock("grey_rough_concrete", false, DyeColor.GRAY, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new RoughConcreteBlock("yellow_rough_concrete", false, DyeColor.YELLOW, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block RED_ROUGH_CONCRETE = new RoughConcreteBlock("red_rough_concrete", false, DyeColor.RED, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block BLUE_ROUGH_CONCRETE = new RoughConcreteBlock("blue_rough_concrete", false, DyeColor.BLUE, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock("yellow_tiles", false, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK = new BaseBuildingBlock("caution_block", false, MeatlibBlockSettings.create(Material.METAL).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK_DOOR = new BaseDoorBlock("caution_block_door", MeatlibBlockSettings.copyOf(CAUTION_BLOCK).nonOpaque(), block());
    public static Block DIRTY_RED_TILES = new BaseBuildingBlock("dirty_red_tiles", false, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block DIRTY_WHITE_TILES = new BaseBuildingBlock("dirty_white_tiles", true, MeatlibBlockSettings.create(Material.STONE).strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block SAND_BRICKS = new BaseBuildingBlock("sandy_bricks", true, MeatlibBlockSettings.copyOf(Blocks.BRICKS));
    public static Block MEAT_STEEL_BLOCK = new BaseBuildingBlock("meat_steel_block", true, MeatlibBlockSettings.copyOf(Blocks.NETHERITE_BLOCK));
    public static Block ASBESTOS = new BaseBuildingBlock("asbestos", false, MeatlibBlockSettings.copyOf(Blocks.STONE));
    public static Block ASBESTOS_TILE = new BaseBuildingBlock("asbestos_tile", false, MeatlibBlockSettings.copyOf(ASBESTOS));
    public static Block CORRUGATED_ASBESTOS = new BaseBuildingBlock("corrugated_asbestos", true, MeatlibBlockSettings.copyOf(ASBESTOS));
    public static PaintedBlockManager<?> PAINTED_CORRUGATED_ASBESTOS = new PaintedBlockManager<>("painted_corrugated_asbestos", PaintedCorrugatedAsbestosBlock::new, MeatlibBlockSettings.copyOf(CORRUGATED_ASBESTOS));
    public static Block ASBESTOS_SHINGLE = new BaseBuildingBlock("asbestos_shingle", false, MeatlibBlockSettings.copyOf(ASBESTOS));

    public static Block BLOODY_BRICKS = new BaseBuildingBlock("bloody_bricks", true, MeatlibBlockSettings.copyOf(Blocks.BRICKS));
    public static Block BLOODY_TILES = new BaseBuildingBlock("bloody_tiles", false, MeatlibBlockSettings.copyOf(BLOODY_BRICKS));

    public static Block REINFORCED_GLASS = new BaseBuildingBlock("reinforced_glass", false, MeatlibBlockSettings.create(Material.GLASS).strength(1f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never))
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


//    public static Block FILLED_SCAFFOLD = new BaseBuildingBlock("filled_scaffold", 64, false, MeatlibBlockSettings.of(Material.METAL).strength(5.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE));

    public static MetalScaffoldingBlock SCAFFOLD_PLATFORM = new MetalScaffoldingBlock("rusted_metal_scaffold", block(), MeatlibBlockSettings.create(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));
    public static MetalScaffoldingBlock BLUE_SCAFFOLD = new MetalScaffoldingBlock("blue_metal_scaffold", block(), MeatlibBlockSettings.create(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));
    public static MetalScaffoldingBlock YELLOW_SCAFFOLD = new MetalScaffoldingBlock("yellow_metal_scaffold", block(), MeatlibBlockSettings.create(Material.METAL).strength(1.5f).sounds(NMSoundGroups.METAL));

    public static Block RUSTY_VENT = BlockRegistry.queue(new BaseColumnBlock("rusty_column", block(), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque()));
    public static Block MESH_PANE = BlockRegistry.queue(new BasePaneBlock("mesh_pane", block(), MeatlibBlockSettings.create(Material.METAL).strength(3.5f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTED_BARS = BlockRegistry.queue(new BasePaneBlock("rusted_bars", block(), MeatlibBlockSettings.create(Material.METAL).strength(3.5f).sounds(NMSoundGroups.METAL)));
    public static Block RUSTY_PANEL = BlockRegistry.queue(new BaseBlock("rusty_panel", MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static Block RUSTY_GRATE = BlockRegistry.queue(new BaseBlock("rusty_vent", MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK)));

    // Decorations
    public static Block DIRTY_SINK = BlockRegistry.queue(new BaseBlock("dirty_sink", block(), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).solid().nonOpaque()));
    public static Block SMALL_SINK = BlockRegistry.queue(new SinkBlock("small_sink", block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static Block TELEVISION_OFF = BlockRegistry.queue(new TelevisionBlock("television_off", block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static Block TELEVISION_STATIC = BlockRegistry.queue(new TelevisionBlock("television_static", block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static BigBlock<?> LARGE_FAN = BlockRegistry.queue(new LargeFanBlock("large_fan", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque()));

//    public static Block SLOPE_TEST = BlockRegistry.queue(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, MeatlibBlockSettings.of(Material.METAL).nonOpaque()));

    public static Block SCAFFOLD_TRAPDOOR = BlockRegistry.queue(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", block(), MeatlibBlockSettings.create(Material.METAL).strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque()));

    public static Block RUSTY_METAL_LADDER = BlockRegistry.queue(new MetalLadderBlock("rusty_metal_ladder", block(), MeatlibBlockSettings.create(Material.METAL).strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque()));
    public static Block RUSTY_METAL_RUNGS = BlockRegistry.queue(new MetalRungsBlock("rusty_metal_rungs", block(), MeatlibBlockSettings.create(Material.METAL).strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque()));

//    public static Block CAUTION_TAPE = BlockRegistry.queue(new CautionTapeBlock("caution_tape", 64, false, MeatlibBlockSettings.of(Material.CARPET).strength(1.0f).sounds(BlockSoundGroup.STONE).nonOpaque()));


    // --- Machines
//    public static Block TROMMEL = BlockRegistry.queue(new TrommelBlock("trommel", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block TROMMEL_STRUCTURE = BlockRegistry.queue(new TrommelBlock.Structure("trommel_top", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SMALL_TROMMEL = BlockRegistry.queue(new SmallTrommelBlock("small_trommel", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SMALL_TROMMEL_STRUCTURE = BlockRegistry.queue(new SmallTrommelBlock.Structure("small_trommel_structure", MeatlibBlockSettings.copyOf(SMALL_TROMMEL)));

//    public static Block CENTRIFUGE = BlockRegistry.queue(new CentrifugeBlock("centrifuge", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block HEATER = BlockRegistry.queue(new HeaterBlock("heater", block().requiresVascular().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block LINEAR_OSCILLATOR = BlockRegistry.queue(new LinearOscillatorBlock("breaker", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block MOTOR = BlockRegistry.queue(new MotorBlock("motor_unit", block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ADVANCED_MOTOR = BlockRegistry.queue(new AdvancedMotorBlock("advanced_motor", block().tooltip(TooltipSupplier.hidden(2)).requiresVascular(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static LargeMotorBlock LARGE_MOTOR = BlockRegistry.queue(new LargeMotorBlock("large_motor", block().requiresVascular().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block FLYWHEEL = BlockRegistry.queue(new FlywheelBlock("large_flywheel", block().tooltip(TooltipSupplier.simple(0)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block STIRLING_ENGINE = BlockRegistry.queue(new StirlingEngineBlock("stirling_engine", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block DEPLOYER = BlockRegistry.queue(new DeployerBlock("deployer", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block AGITATOR = BlockRegistry.queue(new AgitatorBlock("agitator", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block CRUSHER = BlockRegistry.queue(new GrinderBlock("grinder", block().requiresMotor().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Multiblock2ControllerBlock<LargeCrusherStructureBlock> LARGE_CRUSHER = BlockRegistry.queue(new LargeCrusherBlock("large_crusher", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ALLOY_KILN = BlockRegistry.queue(new AlloyKilnBlock("alloy_kiln", block(), MeatlibBlockSettings.copyOf(NMBlocks.SAND_BRICKS)));
    public static Block CRUCIBLE = BlockRegistry.queue(new CrucibleBlock("crucible", block(), FLUID_PIPE_SETTINGS));
    public static Block COLLECTOR = BlockRegistry.queue(new FatCollectorBlock("collector", block(), FLUID_PIPE_SETTINGS));
    public static AssemblerBlock ASSEMBLER = BlockRegistry.queue(new AssemblerBlock("assembler", block().requiresVascular(), MACHINE_SETTINGS));

    public static TallBlock FLUID_EXCITER = BlockRegistry.queue(new FluidExciterBlock("fluid_exciter", block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.create(Material.METAL).sounds(NMSoundGroups.MECHANICAL_MACHINE).hardness(4.0f)));

    public static Block TRANSDUCER = BlockRegistry.queue(new TransducerBlock("transducer", block(), MACHINE_SETTINGS));
    public static final MeatlibBlockSettings POWER_FLOWER_SETTINGS = (MeatlibBlockSettings) MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS).strength(2.0f);
    public static Block POWER_FLOWER_SEEDS = BlockRegistry.queue(new PowerFlowerSeedsBlock("power_flower_seeds", block().tooltip(TooltipSupplier.simple(1)), POWER_FLOWER_SETTINGS.copy().tags(HOE_MINEABLE).hardness(0.01f)));
    public static PowerFlowerGrowthBlock POWER_FLOWER_GROWTH = BlockRegistry.queue(new PowerFlowerGrowthBlock("power_flower_growth", block().tooltip(TooltipSupplier.hidden(1)), POWER_FLOWER_SETTINGS.copy()));
    public static Block POWER_FLOWER_CONTROLLER = BlockRegistry.queue(new PowerFlowerControllerBlock("power_flower_controller", block().tooltip(TooltipSupplier.hidden(1)), POWER_FLOWER_SETTINGS.copy()));
    public static Block POWER_FLOWER_FLUID_PORT = BlockRegistry.queue(new PowerFlowerFluidPortBlock("power_flower_fluid_port", block().tooltip(TooltipSupplier.simple(1)), POWER_FLOWER_SETTINGS.copy()));

    public static Block PEDESTAL = BlockRegistry.queue(new PedestalBlock("pedestal", block().plc(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block CHARNEL_COMPACTOR = BlockRegistry.queue(new CharnelCompactorBlock("charnel_compactor", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static CharnelPumpBlock CHARNEL_PUMP = BlockRegistry.queue(new CharnelPumpBlock("charnel_pump", block(), MeatlibBlockSettings.copy(RUSTY_METAL_BLOCK)));
    public static WellHeadBlock WELL_HEAD = BlockRegistry.queue(new WellHeadBlock("well_head", block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static Block CONTAMINATED_DIRT = BlockRegistry.queue(new ContaminatedDirtBlock("contaminated_dirt", block(), MeatlibBlockSettings.copyOf(Blocks.DIRT).tags(SHOVEL_MINEABLE).strength(3).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS)));
    public static Block WRITHING_EARTH_SPOUT = BlockRegistry.queue(new WrithingEarthSpoutBlock("writhing_earth_spout", block(), MeatlibBlockSettings.copyOf(Blocks.DIRT).tags(SHOVEL_MINEABLE).strength(8).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS)));
    public static Block WRITHING_STONE = BlockRegistry.queue(new WrithingStoneBlock("writhing_stone", block(), MeatlibBlockSettings.copyOf(Blocks.STONE).tags(PICKAXE_MINEABLE).strength(4).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS)));

    public static PhageRayBlock PHAGE_RAY = BlockRegistry.queue(new PhageRayBlock("phage_ray", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static MixerBlock MIXER = BlockRegistry.queue(new MixerBlock("mixer", block().tooltip(TooltipSupplier.hidden(4)).requiresMotor(), MeatlibBlockSettings.copyOf(BRASS_BLOCKS)));

    public static Block CASTING_BASIN = BlockRegistry.queue(new CastingBasinBlock("casting_basin", block(), MeatlibBlockSettings.copyOf(MIXER)));
    public static TallBlock HYDRAULIC_PRESS = BlockRegistry.queue(new HydraulicPressBlock("hydraulic_press", block(), MeatlibBlockSettings.copyOf(MIXER)));

    public static Block WORKSTATION = BlockRegistry.queue(new WorkstationBlock("workstation", ItemSettings.block().tooltip(TooltipSupplier.hidden(2)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block DEATH_BLADES = BlockRegistry.queue(new DeathBladesBlock("death_blades", block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block FEEDING_TROUGH = BlockRegistry.queue(new TroughBlock("feeding_trough", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(FLUID_MACHINE_SETTINGS)));
    public static Block SEPARATOR = BlockRegistry.queue(new SeparatorBlock("separator", block().tooltip(TooltipSupplier.hidden(2)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

//    public static Block SIFTER = BlockRegistry.queue(new SifterBlock("sifter", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block SIFTER_HOPPER = BlockRegistry.queue(new SifterHopperBlock("hopper", 64, true, MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block PYLON = BlockRegistry.queue(new PylonBlock("pylon", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SYNTHESISER = BlockRegistry.queue(new SynthesiserBlock("synthesiser", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block MINCER = BlockRegistry.queue(new MincerBlock("mincer", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ITEM_MINCER = BlockRegistry.queue(new ItemMincerBlock("item_mincer", block().tooltip(TooltipSupplier.hidden(1)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block HOMOGENISER = BlockRegistry.queue(new HomogeniserBlock("homogeniser", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block SURGERY_PLATFORM = BlockRegistry.queue(new SurgeryPlatformBlock("surgery_platform", block().plc(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block UPGRADE_MANAGER = BlockRegistry.queue(new UpgradeManagerBlock("upgrade_manager", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

//    public static Block VAT_CASING = BlockRegistry.queue(new VatCasingBlock("vat_casing", block().tooltip(TooltipSupplier.simple(1)), VAT_SETTINGS));
//    public static Block VAT_CONTROLLER = BlockRegistry.queue(new VatControllerBlock("vat_controller", block(), VAT_SETTINGS));
//    public static Block VAT_ITEM_PORT = BlockRegistry.queue(new ItemPortBlock("vat_item_port", block(), VAT_SETTINGS));
//    public static Block VAT_FLUID_PORT = BlockRegistry.queue(new FluidPortBlock("vat_fluid_port", block(), VAT_SETTINGS));
//    public static Block VAT_WINDOW = BlockRegistry.queue(new VatWindowBlock("clear_tank_wall", block(), AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never)));

    public static Block FLAME_JET = BlockRegistry.queue(new FlameJetBlock("flame_jet", block().factory(FluidComponentItem::new), MACHINE_SETTINGS));

    public static Block ITEM_BUFFER = BlockRegistry.queue(new DisplayPlatformBlock("item_buffer", block().plc(), MACHINE_SETTINGS));
    //    public static Block SPIGOT = BlockRegistry.queue(new SpigotBlock("spigot", 64, false, FLUID_MACHINE_SETTINGS));

    public static Block BOTTLER = BlockRegistry.queue(new BottlerBlock("bottler", block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), FLUID_MACHINE_SETTINGS));

    public static Block FLUID_RATIONER = BlockRegistry.queue(new FluidRationerBlock("fluid_rationer", block().tooltip(TooltipSupplier.hidden(2)), MeatlibBlockSettings.copyOf(FLUID_MACHINE_SETTINGS).nonOpaque()));

    // --- Item Transfer ---
    public static Block ITEM_DUCT = BlockRegistry.queue(new ItemDuctBlock("item_duct", block(), MeatlibBlockSettings.copyOf(Blocks.HOPPER)));
    public static Block PNEUMATIC_TUBE = BlockRegistry.queue(new ItemPipeBlock("item_pipe", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(ITEM_PIPE_SETTINGS)));
    public static Block MERGE_ITEM_PIPE = BlockRegistry.queue(new MergePipeBlock("merge_item_pipe", block(), MeatlibBlockSettings.copyOf(ITEM_PIPE_SETTINGS)));
    public static Block ITEM_PUMP = BlockRegistry.queue(new ItemPumpBlock("item_pump", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block EJECTOR = BlockRegistry.queue(new EjectorBlock("ejector", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block ROUTER = BlockRegistry.queue(new RouterBlock("router", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block BUFFER = BlockRegistry.queue(new BufferBlock("buffer", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.CHEST).tags(AXE_MINEABLE)));
    public static Block CHUTE = BlockRegistry.queue(new ChuteBlock("chute", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.HOPPER)));
    public static Block METAL_BARREL = BlockRegistry.queue(new MetalBarrelBlock("metal_barrel", MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK)));
    public static Block CONTENT_DETECTOR = BlockRegistry.queue(new InventoryDetectorBlock("content_detector", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OBSERVER)));
    public static Block SOLIDITY_DETECTOR = BlockRegistry.queue(new SolidityDetectorBlock("solidity_detector", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OBSERVER)));
    public static Block DUMPER = BlockRegistry.queue(new DumperBlock("dumper", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OAK_WOOD).tags(AXE_MINEABLE)));

    // --- Data ---
    public static Block DATA_CABLE = BlockRegistry.queue(new DataCableBlock("data_cable", block(), VASCULAR_CONDUIT_SETTINGS));
    public static Block VASCULAR_CONDUIT = BlockRegistry.queue(new VascularConduitBlock("vascular_conduit", block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS));
    public static Block ENCASED_VASCULAR_CONDUIT = BlockRegistry.queue(new EncasedVascularConduitBlock("encased_vascular_conduit", block().tooltip(TooltipSupplier.simple(1)), VASCULAR_CONDUIT_SETTINGS));
    public static Block POWER_EMITTER = BlockRegistry.queue(new BaseBlock("power_emitter", block().tooltip(TooltipSupplier.simple(1)), VASCULAR_CONDUIT_SETTINGS));
    public static Block VSC = BlockRegistry.queue(new VSCBlock("vsc", block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS));

    // --- Crops ---
    public static BaseCropBlock WHISPER_WHEAT = BlockRegistry.queue(new BaseCropBlock("whisper_wheat", 64, 0, MeatlibBlockSettings.copyOf(Blocks.WHEAT).tags()));
    public static BaseCropBlock FLESH_POTATO = BlockRegistry.queue(new BaseCropBlock("flesh_potato", "flesh_potato", 64, 2, MeatlibBlockSettings.copyOf(Blocks.POTATOES).tags()));
//    public static Block ROCKWART = BlockRegistry.queue(new BaseCropBlock("rockwart", 64, true, AbstractBlock.Settings.copy(Blocks.WHEAT)));
    public static Block BLOOD_BUBBLE_SAPLING = BlockRegistry.queue(new BaseSaplingBlock("blood_bubble_sapling", new BloodBubbleTreeGenerator(), block(), MeatlibBlockSettings.copyOf(Blocks.WARPED_FUNGUS).tags().ticksRandomly()));

//    public static final AbstractBlock.Settings BB_SETTINGS = MeatlibBlockSettings.create().strength(1.1f).sounds(BlockSoundGroup.METAL);
    public static Block BLOOD_BUBBLE_LOG = BlockRegistry.queue(BlockRegistry.createLogBlock("blood_bubble_log", TooltipSupplier.blank()));
    public static Block BLOOD_BUBBLE_WOOD = BlockRegistry.queue(BlockRegistry.createLogBlock("blood_bubble_wood", TooltipSupplier.blank()));
    public static Block BLOOD_BUBBLE_LEAVES = BlockRegistry.queue(new BloodBubbleLeavesBlock("blood_bubble_leaves", MeatlibBlockSettings.copyOf(Blocks.AZALEA_LEAVES).tags(Set.of(FabricMineableTags.SHEARS_MINEABLE, LEAVES)).sounds(BlockSoundGroup.AZALEA_LEAVES)));
    public static Block BLOOD_BUBBLE_LEAVES_FLOWERING = BlockRegistry.queue(new BloodBubbleLeavesBlock.FruitingBloodBubbleLeavesBlock("blood_bubble_leaves_flowering", MeatlibBlockSettings.copyOf(BLOOD_BUBBLE_LEAVES).sounds(BlockSoundGroup.SLIME)));
    public static Block BLOOD_BUBBLE_PLANKS = new BaseBuildingBlock("blood_bubble_planks", true, MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).strength(2.0f).sounds(BlockSoundGroup.WOOD));
    public static Block BLOOD_BUBBLE_TRAPDOOR = BlockRegistry.queueWithItem(new TrapdoorBlock(MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD)), "blood_bubble_planks_trapdoor");
    public static Block BLOOD_BUBBLE_DOOR = BlockRegistry.queue(new BaseDoorBlock("blood_bubble_planks_door", MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD).nonOpaque(), block()));
    public static Block BLOOD_BUBBLE_BUTTON = BlockRegistry.queueWithItem(new WoodenButtonBlock(MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(1.0f).sounds(BlockSoundGroup.WOOD)), "blood_bubble_planks_button");
    public static Block BLOOD_BUBBLE_FENCE_GATE = BlockRegistry.queueWithItem(new FenceGateBlock(MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD)), "blood_bubble_planks_fence_gate");
    public static Block BLOOD_BUBBLE_PRESSURE_PLATE = BlockRegistry.queueWithItem(new BloodBubblePressurePlate(PressurePlateBlock.ActivationRule.EVERYTHING, MeatlibBlockSettings.create(Material.WOOD, AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).solid().strength(1.0f).sounds(BlockSoundGroup.WOOD)), "blood_bubble_planks_pressure_plate", block().tooltip(TooltipSupplier.simple(1)));

    // --- Assembly ---
//    public static Block LINEAR_RAIL = BlockRegistry.queue(new LinearRailBlock("linear_rail", 64, false, MeatlibBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = BlockRegistry.queue(new BigLeverBlock("big_lever", MeatlibBlockSettings.create(Material.METAL).strength(4.0f)));

    // --- Integrator ---
    public static Block INTEGRATOR_EGG = BlockRegistry.queue(new IntegratorBlock("integrator_egg", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.create(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.SLIME)));

//    public static Block ADVANCED_INTEGRATOR_EGG = BlockRegistry.queue(new AdvancedIntegratorEggBlock("advanced_integrator_egg", block(), MeatlibBlockSettings.copyOf(INTEGRATOR_EGG)));
    public static BigBlock<AdvancedIntegratorStructure> ADVANCED_INTEGRATOR = BlockRegistry.queue(new AdvancedIntegratorBlock("advanced_integrator", MeatlibBlockSettings.create(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = ADVANCED_INTEGRATOR.getStructure();
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = BlockRegistry.queue(new BigBlockStructure(ADVANCED_INTEGRATOR, "advanced_integrator_structure", MeatlibBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));

    public static Block STATUE = BlockRegistry.queue(new StatueBlock("statue", block(), MeatlibBlockSettings.copyOf(Blocks.PRISMARINE)));

    public static Block DIRTY_TOILET = BlockRegistry.queue(new ToiletBlock("dirty_toilet", block(), MeatlibBlockSettings.copyOf(Blocks.STONE)));

    public static Block CRONENCAKE = BlockRegistry.queue(new CronenCakeBlock("cronencake", MeatlibBlockSettings.copyOf(Blocks.CAKE).tags().sounds(BlockSoundGroup.SLIME)));
    public static Block CANDLE_CRONENCAKE = BlockRegistry.queue(new CandleCronenCakeBlock("candle_cronencake", MeatlibBlockSettings.copyOf(Blocks.CANDLE_CAKE).tags().sounds(BlockSoundGroup.SLIME)));

//    public static Block ROUTE_TEST = BlockRegistry.queue(new RouteTestBlock("routing_test", MeatlibBlockSettings.of(Material.METAL)));

    public static Block HOLDING_TRACK = BlockRegistry.queue(new HoldingTrackBlock("holding_track", block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.RAIL)));

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

