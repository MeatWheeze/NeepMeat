package com.neep.neepmeat.init;

import com.neep.meatlib.block.*;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.Path;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
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
import com.neep.neepmeat.machine.fabricator.FabricatorBlock;
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
import com.neep.neepmeat.machine.small_compressor.SmallCompressorBlock;
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
import com.neep.neepmeat.transport.block.item_transport.DisplayPlateBlock;
import com.neep.neepmeat.transport.block.item_transport.ItemDuctBlock;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Set;

import static net.minecraft.registry.tag.BlockTags.*;

@SuppressWarnings("unused")
@RegisterMe(value = NeepMeat.NAMESPACE)
public class NMBlocks
{
    public static final RegistrationContext C = NeepMeat.C;

    // --- Building Blocks ---
    public static final AbstractBlock.Settings METAL = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().hardness(3.0f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings BRASS_BLOCKS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(1.8f).sounds(BlockSoundGroup.NETHERITE);
    public static final AbstractBlock.Settings FLUID_PIPE_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings VASCULAR_CONDUIT_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(1.1f).sounds(NMSoundGroups.METAL);
    public static final AbstractBlock.Settings MACHINE_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(3.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE).nonOpaque().solidBlock(InventoryDetectorBlock::never);
    public static final AbstractBlock.Settings OPAQUE_MACHINE_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(3.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
//    public static final AbstractBlock.Settings VAT_SETTINGS = MeatlibBlockSettings.of(Material.METAL).strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings FLUID_MACHINE_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(2.5f).sounds(NMSoundGroups.MECHANICAL_MACHINE);
    public static final AbstractBlock.Settings ITEM_PIPE_SETTINGS = MeatlibBlockSettings.create(BlockTags.PICKAXE_MINEABLE).solid().strength(1.1f).sounds(BlockSoundGroup.METAL);

//    public static Block DUAT_STONE = new BaseBuildingBlock("duat_stone", true, MeatlibBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_COBBLESTONE = new BaseBuildingBlock("duat_cobblestone", true, MeatlibBlockSettings.of(Material.STONE).strength(2.5f).sounds(BlockSoundGroup.STONE));
//    public static Block DUAT_GRAVEL = new BaseBlock("duat_gravel", block(), MeatlibBlockSettings.of(Material.STONE).strength(1.0f).sounds(BlockSoundGroup.GRAVEL)));

//    public static Block OBJ_TEST = new BigBlock("obj_test", MeatlibBlockSettings.copyOf(Blocks.STONE)));

//    public static Block TEST_MULTIBLOCK = new TestMultiblock("test_multiblock", MeatlibBlockSettings.create()));

    public static PaintedBlockManager<?> SMOOTH_TILE = new PaintedBlockManager<>(C, "smooth_tile", SmoothTileBlock::new, MeatlibBlockSettings.create().hardness(3.0f));

    @Path("polished_metal_bricks") public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
    @Path("polished_metal_small_bricks") public static Block POLISHED_MERAL_SMALL_BRICKS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
    @Path("polished_metal") public static BaseBuildingBlock POLISHED_METAL = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
    @Path("blue_polished_metal") public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
    @Path("rusty_metal") public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock(C, false, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
    public static Block RUSTY_METAL_DOOR = new BaseDoorBlock(C, MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque(), block(), NMBlockSets.RUSTY_METAL);
    public static Block WHITE_ROUGH_CONCRETE = new RoughConcreteBlock(C, false, DyeColor.WHITE, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block GREY_ROUGH_CONCRETE = new RoughConcreteBlock(C, false, DyeColor.GRAY, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new RoughConcreteBlock(C, false, DyeColor.YELLOW, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block RED_ROUGH_CONCRETE = new RoughConcreteBlock(C, false, DyeColor.RED, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block BLUE_ROUGH_CONCRETE = new RoughConcreteBlock(C, false, DyeColor.BLUE, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock(C, false, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK = new BaseBuildingBlock(C, false, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block CAUTION_BLOCK_DOOR = new BaseDoorBlock(C, MeatlibBlockSettings.copyOf(CAUTION_BLOCK).nonOpaque(), block(), NMBlockSets.RUSTY_METAL);
    public static Block DIRTY_RED_TILES = new BaseBuildingBlock(C, false, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block DIRTY_WHITE_TILES = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create().strength(1.8f).sounds(BlockSoundGroup.STONE));
    public static Block SANDY_BRICKS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.copyOf(Blocks.BRICKS));
    public static Block MEAT_STEEL_BLOCK = new BaseBuildingBlock(C, true, MeatlibBlockSettings.copyOf(Blocks.NETHERITE_BLOCK));
    public static Block ASBESTOS = new BaseBuildingBlock(C, false, MeatlibBlockSettings.copyOf(Blocks.STONE));
    public static Block ASBESTOS_TILE = new BaseBuildingBlock(C, false, MeatlibBlockSettings.copyOf(ASBESTOS));
    public static Block CORRUGATED_ASBESTOS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.copyOf(ASBESTOS));
    public static PaintedBlockManager<?> PAINTED_CORRUGATED_ASBESTOS = new PaintedBlockManager<>(C, "painted_corrugated_asbestos", PaintedCorrugatedAsbestosBlock::new, MeatlibBlockSettings.copyOf(CORRUGATED_ASBESTOS));
    public static Block ASBESTOS_SHINGLE = new BaseBuildingBlock(C, false, MeatlibBlockSettings.copyOf(ASBESTOS));

    public static Block BLOODY_BRICKS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.copyOf(Blocks.BRICKS));
    public static Block BLOODY_TILES = new BaseBuildingBlock(C, false, MeatlibBlockSettings.copyOf(BLOODY_BRICKS));

    public static Block REINFORCED_GLASS = new BaseBuildingBlock(C, false, MeatlibBlockSettings.create().strength(1f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never))
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
        public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
        {
            return true;
        }
    };


//    public static Block FILLED_SCAFFOLD = new BaseBuildingBlock("filled_scaffold", 64, false, MeatlibBlockSettings.of(Material.METAL).strength(5.0f).sounds(NMSoundGroups.MECHANICAL_MACHINE));

    @Path("rusted_metal_scaffold") public static MetalScaffoldingBlock SCAFFOLD_PLATFORM = new MetalScaffoldingBlock(C, block(), MeatlibBlockSettings.create().strength(1.5f).sounds(NMSoundGroups.METAL));
    @Path("blue_metal_scaffold") public static MetalScaffoldingBlock BLUE_SCAFFOLD = new MetalScaffoldingBlock(C, block(), MeatlibBlockSettings.create().strength(1.5f).sounds(NMSoundGroups.METAL));
    @Path("yellow_metal_scaffold") public static MetalScaffoldingBlock YELLOW_SCAFFOLD = new MetalScaffoldingBlock(C, block(), MeatlibBlockSettings.create().strength(1.5f).sounds(NMSoundGroups.METAL));

    @Path("rusty_column") public static Block RUSTY_VENT = new BaseColumnBlock(C, block(), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque());
    public static Block MESH_PANE = new BasePaneBlock(C, block(), MeatlibBlockSettings.create().strength(3.5f).sounds(BlockSoundGroup.LANTERN));
    public static Block RUSTED_BARS = new BasePaneBlock(C, block(), MeatlibBlockSettings.create().strength(3.5f).sounds(NMSoundGroups.METAL));
    public static Block RUSTY_PANEL = new BaseBlock(C, MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK));
    @Path("rusty_vent") public static Block RUSTY_GRATE = new BaseBlock(C, MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK));

    // Decorations
    public static Block DIRTY_SINK = new BaseBlock(C, block(), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).solid().nonOpaque());
    public static Block SMALL_SINK = new SinkBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque());
    public static Block TELEVISION_OFF = new TelevisionBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque());
    public static Block TELEVISION_STATIC = new TelevisionBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.STONE).nonOpaque());
    @Path("large_fan") public static BigBlock<?> LARGE_FAN = new LargeFanBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK).nonOpaque());

//    public static Block SLOPE_TEST = new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, MeatlibBlockSettings.of(Material.METAL).nonOpaque()));

    @Path("rusted_metal_scaffold_trapdoor") public static Block SCAFFOLD_TRAPDOOR = new ScaffoldTrapdoorBlock(C, block(), MeatlibBlockSettings.create().strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque());

    public static Block RUSTY_METAL_LADDER = new MetalLadderBlock(C, block(), MeatlibBlockSettings.create().strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque());
    public static Block RUSTY_METAL_RUNGS = new MetalRungsBlock(C, block(), MeatlibBlockSettings.create().strength(2.0f).sounds(NMSoundGroups.METAL).nonOpaque());

//    public static Block CAUTION_TAPE = new CautionTapeBlock("caution_tape", 64, false, MeatlibBlockSettings.of(Material.CARPET).strength(1.0f).sounds(BlockSoundGroup.STONE).nonOpaque()));


    // --- Machines
//    public static Block TROMMEL = new TrommelBlock("trommel", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block TROMMEL_STRUCTURE = new TrommelBlock.Structure("trommel_top", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    public static Block SMALL_TROMMEL = new SmallTrommelBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    public static Block SMALL_TROMMEL_STRUCTURE = new SmallTrommelBlock.Structure(MeatlibBlockSettings.copyOf(SMALL_TROMMEL));

//    public static Block CENTRIFUGE = new CentrifugeBlock("centrifuge", MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    public static Block HEATER = new HeaterBlock(C, block().requiresVascular().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("breaker") public static Block LINEAR_OSCILLATOR = new LinearOscillatorBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("motor_unit") public static Block MOTOR = new MotorBlock(C, block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    public static Block ADVANCED_MOTOR = new AdvancedMotorBlock(C, block().tooltip(TooltipSupplier.hidden(2)).requiresVascular(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("large_motor") public static LargeMotorBlock LARGE_MOTOR = new LargeMotorBlock(C, block().requiresVascular().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("large_flywheel") public static Block FLYWHEEL = new FlywheelBlock(C, block().tooltip(TooltipSupplier.simple(0)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("stirling_engine") public static Block STIRLING_ENGINE = new StirlingEngineBlock(C, block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("deployer") public static Block DEPLOYER = new DeployerBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
//    public static Block AGITATOR = new AgitatorBlock("agitator", block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
    @Path("grinder") public static Block CRUSHER = new GrinderBlock(C, block().requiresMotor().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("large_crusher") public static Multiblock2ControllerBlock<LargeCrusherStructureBlock> LARGE_CRUSHER = new LargeCrusherBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("alloy_kiln") public static Block ALLOY_KILN = new AlloyKilnBlock(C, block(), MeatlibBlockSettings.copyOf(NMBlocks.SANDY_BRICKS));
    @Path("crucible") public static Block CRUCIBLE = new CrucibleBlock(C, block(), FLUID_PIPE_SETTINGS);
    @Path("collector") public static Block COLLECTOR = new FatCollectorBlock(C, block(), FLUID_PIPE_SETTINGS);
    @Path("assembler") public static AssemblerBlock ASSEMBLER = new AssemblerBlock(C, block().tooltip(TooltipSupplier.hidden(1)).requiresVascular(), MACHINE_SETTINGS);
    @Path("fabricator") public static Block FABRICATOR = new FabricatorBlock(C, block().tooltip(TooltipSupplier.hidden(1)), MACHINE_SETTINGS);

    @Path("fluid_exciter") public static TallBlock FLUID_EXCITER = new FluidExciterBlock(C, block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.create().sounds(NMSoundGroups.MECHANICAL_MACHINE).hardness(4.0f));

    @Path("transducer") public static Block TRANSDUCER = new TransducerBlock(C, block(), MACHINE_SETTINGS);
    public static final MeatlibBlockSettings POWER_FLOWER_SETTINGS = (MeatlibBlockSettings) MeatlibBlockSettings.create(AXE_MINEABLE).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS).strength(2.0f);
    @Path("power_flower_seeds") public static Block POWER_FLOWER_SEEDS = new PowerFlowerSeedsBlock(C, block().tooltip(TooltipSupplier.simple(1)), POWER_FLOWER_SETTINGS.copy().tags(HOE_MINEABLE).hardness(0.01f));
    @Path("power_flower_growth") public static PowerFlowerGrowthBlock POWER_FLOWER_GROWTH = new PowerFlowerGrowthBlock(C, block().tooltip(TooltipSupplier.hidden(1)), POWER_FLOWER_SETTINGS.copy());
    @Path("power_flower_controller") public static Block POWER_FLOWER_CONTROLLER = new PowerFlowerControllerBlock(C, block().tooltip(TooltipSupplier.hidden(1)), POWER_FLOWER_SETTINGS.copy());
    @Path("power_flower_fluid_port") public static Block POWER_FLOWER_FLUID_PORT = new PowerFlowerFluidPortBlock(C, block().tooltip(TooltipSupplier.simple(1)), POWER_FLOWER_SETTINGS.copy());

    @Path("pedestal") public static Block PEDESTAL = new PedestalBlock(C, block().plc(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

    @Path("charnel_compactor") public static Block CHARNEL_COMPACTOR = new CharnelCompactorBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK));
    @Path("charnel_pump") public static CharnelPumpBlock CHARNEL_PUMP = new CharnelPumpBlock(C, block(), MeatlibBlockSettings.copy(RUSTY_METAL_BLOCK));
    @Path("well_head") public static WellHeadBlock WELL_HEAD = new WellHeadBlock(C, block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK));
    @Path("contaminated_dirt") public static Block CONTAMINATED_DIRT = new ContaminatedDirtBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.DIRT).tags(SHOVEL_MINEABLE).strength(3).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS));
    @Path("writhing_earth_spout") public static Block WRITHING_EARTH_SPOUT = new WrithingEarthSpoutBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.DIRT).tags(SHOVEL_MINEABLE).strength(8).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS));
    @Path("writhing_stone") public static Block WRITHING_STONE = new WrithingStoneBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.STONE).tags(PICKAXE_MINEABLE).strength(4).sounds(BlockSoundGroup.MUDDY_MANGROVE_ROOTS));

    @Path("phage_ray") public static PhageRayBlock PHAGE_RAY = new PhageRayBlock(C, MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

    @Path("mixer") public static MixerBlock MIXER = new MixerBlock(C, block().tooltip(TooltipSupplier.hidden(4)).requiresMotor(), MeatlibBlockSettings.copyOf(BRASS_BLOCKS));

    @Path("casting_basin") public static Block CASTING_BASIN = new CastingBasinBlock(C, block(), MeatlibBlockSettings.copyOf(MIXER));
    @Path("hydraulic_press") public static TallBlock HYDRAULIC_PRESS = new HydraulicPressBlock(C, block(), MeatlibBlockSettings.copyOf(MIXER));

    @Path("workstation") public static Block WORKSTATION = new WorkstationBlock(C, ItemSettings.block().tooltip(TooltipSupplier.hidden(2)), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

    public static Block DEATH_BLADES = new DeathBladesBlock(C, block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

    public static Block FEEDING_TROUGH = new TroughBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(FLUID_MACHINE_SETTINGS));
    @Path("separator") public static Block SEPARATOR = new SeparatorBlock(C, block().tooltip(TooltipSupplier.hidden(2)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

//    public static Block SIFTER = new SifterBlock("sifter", block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));
//    public static Block SIFTER_HOPPER = new SifterHopperBlock("hopper", 64, true, MeatlibBlockSettings.copyOf(MACHINE_SETTINGS)));

    @Path("pylon") public static Block PYLON = new PylonBlock(C, MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("synthesiser") public static Block SYNTHESISER = new SynthesiserBlock(C, MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("mincer") public static Block MINCER = new MincerBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("item_mincer") public static Block ITEM_MINCER = new ItemMincerBlock(C, block().tooltip(TooltipSupplier.hidden(1)).requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    @Path("homogeniser") public static Block HOMOGENISER = new HomogeniserBlock(C, block().requiresMotor(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

    public static Block SURGERY_PLATFORM = new SurgeryPlatformBlock(C, block().plc(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));
    public static Block UPGRADE_MANAGER = new UpgradeManagerBlock(C, block(), MeatlibBlockSettings.copyOf(MACHINE_SETTINGS));

//    public static Block VAT_CASING = new VatCasingBlock("vat_casing", block().tooltip(TooltipSupplier.simple(1)), VAT_SETTINGS));
//    public static Block VAT_CONTROLLER = new VatControllerBlock("vat_controller", block(), VAT_SETTINGS));
//    public static Block VAT_ITEM_PORT = new ItemPortBlock("vat_item_port", block(), VAT_SETTINGS));
//    public static Block VAT_FLUID_PORT = new FluidPortBlock("vat_fluid_port", block(), VAT_SETTINGS));
//    public static Block VAT_WINDOW = new VatWindowBlock("clear_tank_wall", block(), AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(VatWindowBlock::never).solidBlock(VatWindowBlock::never).suffocates(VatWindowBlock::never).blockVision(VatWindowBlock::never)));

    public static Block FLAME_JET = new FlameJetBlock(C, block().factory(FluidComponentItem::new), MACHINE_SETTINGS);

    public static Block ITEM_BUFFER = new DisplayPlateBlock(C, block().tooltip(TooltipSupplier.simple(1)).plc(), MACHINE_SETTINGS.sounds(BlockSoundGroup.METAL));
    //    public static Block SPIGOT = new SpigotBlock("spigot", 64, false, FLUID_MACHINE_SETTINGS));

    public static Block BOTTLER = new BottlerBlock(C, block().tooltip(TooltipSupplier.simple(1)).requiresMotor(), FLUID_MACHINE_SETTINGS);

    public static Block FLUID_RATIONER = new FluidRationerBlock(C, block().tooltip(TooltipSupplier.hidden(2)), MeatlibBlockSettings.copyOf(FLUID_MACHINE_SETTINGS).nonOpaque());

    public static Block SMALL_COMPRESSOR = new SmallCompressorBlock(C, block().tooltip(TooltipSupplier.hidden(1)), MeatlibBlockSettings.copyOf(FLUID_MACHINE_SETTINGS).nonOpaque());

    // --- Item Transfer ---
    @Path("item_duct") public static Block ITEM_DUCT = new ItemDuctBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.HOPPER));
    @Path("chute") public static Block CHUTE = new ChuteBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.HOPPER));
    public static Block METAL_BARREL = new MetalBarrelBlock(C, MeatlibBlockSettings.copyOf(RUSTY_METAL_BLOCK));
    @Path("content_detector") public static Block CONTENT_DETECTOR = new InventoryDetectorBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OBSERVER));
    @Path("solidity_detector") public static Block SOLIDITY_DETECTOR = new SolidityDetectorBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OBSERVER));

    // --- Data ---
    public static Block DATA_CABLE = new DataCableBlock(C, block(), VASCULAR_CONDUIT_SETTINGS);
    public static Block ENCASED_DATA_CABLE = new EncasedDataCableBlock(C, block(), VASCULAR_CONDUIT_SETTINGS);
    public static Block VASCULAR_CONDUIT = new VascularConduitBlock(C, block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS);
    public static Block ENCASED_VASCULAR_CONDUIT = new EncasedVascularConduitBlock(C, block().tooltip(TooltipSupplier.simple(1)), VASCULAR_CONDUIT_SETTINGS);
    public static Block POWER_EMITTER = new BaseBlock(C, block().tooltip(TooltipSupplier.simple(1)), VASCULAR_CONDUIT_SETTINGS);
    public static Block VSC = new VSCBlock(C, block().tooltip(TooltipSupplier.hidden(1)), VASCULAR_CONDUIT_SETTINGS);

    // --- Crops ---
    public static BaseCropBlock WHISPER_WHEAT = new BaseCropBlock(C, 64, 0, MeatlibBlockSettings.copyOf(Blocks.WHEAT).tags());
    @Path("flesh_potato") public static BaseCropBlock FLESH_POTATO = new BaseCropBlock(C, "", 64, 2, MeatlibBlockSettings.copyOf(Blocks.POTATOES).tags());
//    public static Block ROCKWART = new BaseCropBlock("rockwart", 64, true, AbstractBlock.Settings.copy(Blocks.WHEAT)));
    @Path("blood_bubble_sapling") public static Block BLOOD_BUBBLE_SAPLING = new BaseSaplingBlock(C, new BloodBubbleTreeGenerator(), block(), MeatlibBlockSettings.copyOf(Blocks.WARPED_FUNGUS).tags().ticksRandomly());

//    public static final AbstractBlock.Settings BB_SETTINGS = MeatlibBlockSettings.create().strength(1.1f).sounds(BlockSoundGroup.METAL);
    @Path("blood_bubble_log") public static Block BLOOD_BUBBLE_LOG = BlockRegistry.createLogBlock(C, TooltipSupplier.blank());
    @Path("blood_bubble_wood") public static Block BLOOD_BUBBLE_WOOD = BlockRegistry.createLogBlock(C, TooltipSupplier.blank());
    @Path("blood_bubble_leaves") public static Block BLOOD_BUBBLE_LEAVES = new BloodBubbleLeavesBlock(C, MeatlibBlockSettings.copyOf(Blocks.AZALEA_LEAVES).tags(Set.of(FabricMineableTags.SHEARS_MINEABLE, LEAVES)).sounds(BlockSoundGroup.AZALEA_LEAVES));
    @Path("blood_bubble_leaves_flowering") public static Block BLOOD_BUBBLE_LEAVES_FLOWERING = new BloodBubbleLeavesBlock.FruitingBloodBubbleLeavesBlock(C, MeatlibBlockSettings.copyOf(BLOOD_BUBBLE_LEAVES).sounds(BlockSoundGroup.SLIME));
    @Path("blood_bubble_planks") public static Block BLOOD_BUBBLE_PLANKS = new BaseBuildingBlock(C, true, MeatlibBlockSettings.create(AXE_MINEABLE).strength(2.0f).sounds(BlockSoundGroup.WOOD));
    @Path("blood_bubble_planks_trapdoor") public static Block BLOOD_BUBBLE_TRAPDOOR = C.withItem(new TrapdoorBlock(MeatlibBlockSettings.create(AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD), BlockSetType.WARPED), block());
    @Path("blood_bubble_planks_door") public static Block BLOOD_BUBBLE_DOOR = new BaseDoorBlock(C, MeatlibBlockSettings.create(AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD).nonOpaque(), block(), BlockSetType.WARPED);
    @Path("blood_bubble_planks_button") public static Block BLOOD_BUBBLE_BUTTON = C.withItem(new ButtonBlock(MeatlibBlockSettings.create(AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(1.0f).sounds(BlockSoundGroup.WOOD), BlockSetType.WARPED, 20, true), block());
    @Path("blood_bubble_planks_fence_gate") public static Block BLOOD_BUBBLE_FENCE_GATE = C.withItem(new FenceGateBlock(MeatlibBlockSettings.create(AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).strength(2.0f).sounds(BlockSoundGroup.WOOD), WoodType.WARPED), block());
    @Path("blood_bubble_planks_pressure_plate") public static Block BLOOD_BUBBLE_PRESSURE_PLATE = C.withItem(new BloodBubblePressurePlate(PressurePlateBlock.ActivationRule.EVERYTHING, MeatlibBlockSettings.create(AXE_MINEABLE).simpleDrop(ItemRegistry::getMatchingItem).solid().strength(1.0f).sounds(BlockSoundGroup.WOOD)), block().tooltip(TooltipSupplier.simple(1)));

    // --- Assembly ---
//    public static Block LINEAR_RAIL = new LinearRailBlock("linear_rail", 64, false, MeatlibBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = new BigLeverBlock(C, MeatlibBlockSettings.create().strength(4.0f));

    // --- Integrator ---
    @Path("integrator_egg") public static Block INTEGRATOR_EGG = new IntegratorBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.create().strength(2.0f).sounds(BlockSoundGroup.SLIME));

//    public static Block ADVANCED_INTEGRATOR_EGG = new AdvancedIntegratorEggBlock("advanced_integrator_egg", block(), MeatlibBlockSettings.copyOf(INTEGRATOR_EGG)));
    @Path("advanced_integrator") public static BigBlock<AdvancedIntegratorStructure> ADVANCED_INTEGRATOR = new AdvancedIntegratorBlock(C, MeatlibBlockSettings.create().strength(3.0f).sounds(NMSoundGroups.METAL));
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = ADVANCED_INTEGRATOR.getStructure();
//    public static BigBlockStructure ADVANCED_INTEGRATOR_STRUCTURE = new BigBlockStructure(ADVANCED_INTEGRATOR, "advanced_integrator_structure", MeatlibBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));

    @Path("statue") public static Block STATUE = new StatueBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.PRISMARINE));

    public static Block DIRTY_TOILET = new ToiletBlock(C, block(), MeatlibBlockSettings.copyOf(Blocks.STONE));

    public static Block CRONENCAKE = new CronenCakeBlock(C, MeatlibBlockSettings.copyOf(Blocks.CAKE).tags().sounds(BlockSoundGroup.SLIME));
    public static Block CANDLE_CRONENCAKE = new CandleCronenCakeBlock(C, MeatlibBlockSettings.copyOf(Blocks.CANDLE_CAKE).tags().sounds(BlockSoundGroup.SLIME));

//    public static Block ROUTE_TEST = new RouteTestBlock("routing_test", MeatlibBlockSettings.of(Material.METAL)));

    public static Block HOLDING_TRACK = new HoldingTrackBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.RAIL));
    public static Block DUMPING_TRACK = new DumpingTrackBlock(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.RAIL));
    public static Block CONTROL_TRACK = new PlayerControlTrack(C, block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.RAIL));


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

