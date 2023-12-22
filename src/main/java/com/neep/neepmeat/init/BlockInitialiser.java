package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.block.*;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.actuator.LinearRailBlock;
import com.neep.neepmeat.block.machine.FluidDrainBlock;
import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.block.machine.TrommelBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BlockInitialiser
{
    public static final Map<String, NMBlock> BLOCKS = new HashMap<>();

    public static Block MESH_PANE;

    public static Block SCAFFOLD_TRAPDOOR;
    public static Block RUSTED_BARS;
    public static Block RUSTY_VENT;
    public static Block RUSTY_PANEL;
    public static Block RUSTY_GRATE;
    public static BasePaintedBlock SMOOTH_TILE = new BasePaintedBlock("smooth_tile", FabricBlockSettings.of(Material.STONE).hardness(5.0f));

    public static Block SLOPE_TEST;

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

    public static Block TEST;
    public static Block PIPE;
    public static Block TANK;
    public static Block GLASS_TANK;
    public static Block ITEM_BUFFER;
    public static Block FLUID_METER;
    public static Block FLUID_PORT;
    public static Block SPIGOT;

    public static Block ITEM_DUCT;
    public static Block IRON_PIPE;
    public static Block COPPER_PIPE;
    public static Block PUMP;
    public static Block TROMMEL;
    public static Block HEATER;
    public static Block TROMMEL_CENTRE;
    public static Block FLUID_DRAIN;

    public static Block LINEAR_RAIL;

    public static Block INTEGRATOR_EGG;
    public static Block TANK_WALL;

    public static Block registerBlock(String id, Block block)
    {
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, id), block);
    }

    public static Block registerBlock(NMBlock block)
    {
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to register something that wasn't a block.");
        }
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, block.getRegistryName()), (Block) block);
    }

    public static void registerBlocks()
    {

        // --- Building Blocks ---
        RUSTY_VENT = registerBlock(new BaseColumnBlock("rusty_vent", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));
        MESH_PANE = registerBlock(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        RUSTED_BARS = registerBlock(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        RUSTY_PANEL = registerBlock(new BaseBlock("rusty_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        RUSTY_GRATE = registerBlock(new BaseBlock("rusty_grate", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        GREY_SMOOTH_TILE = registerBlock(new BaseBlock("smooth_tile_grey", 64, false, FabricBlockSettings.of(Material.STONE).strength(4.0f).sounds(BlockSoundGroup.STONE)));

        SLOPE_TEST = registerBlock(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

        SCAFFOLD_TRAPDOOR = registerBlock(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));

        // --- General Blocks ---
        PIPE = registerBlock(new FluidPipeBlock("pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        IRON_PIPE = registerBlock(new FluidPipeBlock("iron_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        COPPER_PIPE = registerBlock(new FluidPipeBlock("copper_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        TROMMEL = registerBlock(new TrommelBlock("trommel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
        TROMMEL_CENTRE = registerBlock(new BaseDummyBlock("trommel_centre", FabricBlockSettings.of(Material.METAL).strength(4.0f)));
        HEATER = registerBlock(new HeaterBlock("heater", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        // --- Fluid Transfer ---
        PUMP = registerBlock(new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        TANK = registerBlock(new TankBlock("basic_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        GLASS_TANK = registerBlock(new GlassTankBlock("basic_glass_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        ITEM_BUFFER = registerBlock(new ItemBufferBlock("item_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
        FLUID_METER = registerBlock(new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        FLUID_PORT = registerBlock(new FluidPortBlock("fluid_port", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        FLUID_DRAIN = registerBlock(new FluidDrainBlock("fluid_drain", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        SPIGOT = registerBlock(new SpigotBlock("spigot", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

        // --- Item Transfer ---
        ITEM_DUCT = registerBlock(new ItemDuctBlock("item_duct", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));

        // --- Assembly ---
        LINEAR_RAIL = registerBlock(new LinearRailBlock("linear_rail", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        // --- Integrator ---
        INTEGRATOR_EGG = registerBlock(new IntegratorEggBlock("integrator_egg", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.SLIME)));
        TANK_WALL = registerBlock(new TankWallBlock("clear_tank_wall", 64, false, AbstractBlock.Settings.of(Material.GLASS).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(TankWallBlock::never).solidBlock(TankWallBlock::never).suffocates(TankWallBlock::never).blockVision(TankWallBlock::never)));

        for (NMBlock block : BLOCKS.values())
        {
            registerBlock(block.getRegistryName(), (Block) block);
        }

    }


}
