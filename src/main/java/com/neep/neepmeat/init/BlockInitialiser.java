package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.block.*;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.actuator.LinearRailBlock;
import com.neep.neepmeat.block.machine.FluidDrainBlock;
import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.block.machine.TrommelBlock;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BlockInitialiser
{
    public static final Map<Identifier, NMBlock> BLOCKS = new LinkedHashMap<>();

    public static BasePaintedBlock SMOOTH_TILE = new BasePaintedBlock("smooth_tile", FabricBlockSettings.of(Material.STONE).hardness(5.0f));

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
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
    public static Block RUSTY_VENT = queueBlock(new BaseColumnBlock("rusty_vent", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));
    public static Block MESH_PANE = queueBlock(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTED_BARS = queueBlock(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block RUSTY_PANEL = queueBlock(new BaseBlock("rusty_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block RUSTY_GRATE = queueBlock(new BaseBlock("rusty_grate", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

    public static Block SLOPE_TEST = queueBlock(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

    public static Block SCAFFOLD_TRAPDOOR = queueBlock(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));

    // --- General Blocks ---
    public static Block PIPE = queueBlock(new FluidPipeBlock("pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block IRON_PIPE = queueBlock(new FluidPipeBlock("iron_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block COPPER_PIPE = queueBlock(new FluidPipeBlock("copper_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block TROMMEL = queueBlock(new TrommelBlock("trommel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block TROMMEL_CENTRE = queueBlock(new BaseDummyBlock("trommel_centre", FabricBlockSettings.of(Material.METAL).strength(4.0f)));
    public static Block HEATER = queueBlock(new HeaterBlock("heater", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    // --- Fluid Transfer ---
    public static Block PUMP = queueBlock(new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block TANK = queueBlock(new TankBlock("basic_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block GLASS_TANK = queueBlock(new GlassTankBlock("basic_glass_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block ITEM_BUFFER = queueBlock(new ItemBufferBlock("item_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block FLUID_METER = queueBlock(new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block PRESSURE_GAUGE = queueBlock(new PressureGauge("pressure_gauge", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
    public static Block FLUID_PORT = queueBlock(new FluidPortBlock("fluid_port", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block FLUID_DRAIN = queueBlock(new FluidDrainBlock("fluid_drain", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block SPIGOT = queueBlock(new SpigotBlock("spigot", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

    // --- Item Transfer ---
    public static Block ITEM_DUCT = queueBlock(new ItemDuctBlock("item_duct", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block PNEUMATIC_TUBE = queueBlock(new PneumaticTubeBlock("pneumatic_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
    public static Block ITEM_PUMP = queueBlock(new ItemPumpBlock("item_pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
    public static Block BUFFER = queueBlock(new BufferBlock("buffer", 64, true, FabricBlockSettings.copy(Blocks.CHEST)));
    public static Block CONTENT_DETECTOR = queueBlock(new ContentDetectorBlock("content_detector", 64, true, FabricBlockSettings.copy(Blocks.OBSERVER)));

    // --- Assembly ---
    public static Block LINEAR_RAIL = queueBlock(new LinearRailBlock("linear_rail", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    public static Block BIG_LEVER = queueBlock(new BigLeverBlock("big_lever", FabricBlockSettings.of(Material.METAL).strength(4.0f)));

    // --- Integrator ---
    public static Block INTEGRATOR_EGG = queueBlock(new IntegratorEggBlock("integrator_egg", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.SLIME)));
    public static Block TANK_WALL = queueBlock(new TankWallBlock("clear_tank_wall", 64, false, AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(TankWallBlock::never).solidBlock(TankWallBlock::never).suffocates(TankWallBlock::never).blockVision(TankWallBlock::never)));

    public static Block queueBlock(NMBlock block)
    {
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to register something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(NeepMeat.NAMESPACE, block.getRegistryName()), block);
        return (Block) block;
    }

    public static void registerBlocks()
    {
        for (Map.Entry<Identifier, NMBlock> entry : BLOCKS.entrySet())
        {
//            registerBlock(entry.getKey(), (Block) entry.getValue());
            Registry.register(Registry.BLOCK, entry.getKey(), (Block) entry.getValue());
        }
    }
}
