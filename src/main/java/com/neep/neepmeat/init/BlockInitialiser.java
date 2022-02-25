package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.base.*;
import com.neep.neepmeat.block.machine.FluidDrainBlock;
import com.neep.neepmeat.block.machine.TrommelBlock;
import com.neep.neepmeat.fluid.BloodFluid;
import com.neep.neepmeat.item.FluidHoseItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class BlockInitialiser
{
    public static final Map<String, NMBlock> BLOCKS = new HashMap<>();

    public static Block MESH_PANE;

    public static Block SCAFFOLD_TRAPDOOR;
    public static Block RUSTED_BARS;
    public static Block CAUTION_BLOCK;
    public static Block RUSTY_VENT;

    public static Block SLOPE_TEST;

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("polished_metal_bricks", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block RUSTED_IRON_BLOCK = new BaseBuildingBlock("polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block BLUE_IRON_BLOCK = new BaseBuildingBlock("blue_polished_metal", 64, true, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block RUSTY_METAL_BLOCK = new BaseBuildingBlock("rusty_metal", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));
    public static Block GREY_ROUGH_CONCRETE = new BaseBuildingBlock("grey_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_ROUGH_CONCRETE = new BaseBuildingBlock("yellow_rough_concrete", 64, false, FabricBlockSettings.of(Material.STONE).strength(5.0f).sounds(BlockSoundGroup.STONE));
    public static Block YELLOW_TILES = new BaseBuildingBlock("yellow_tiles", 64, false, FabricBlockSettings.of(Material.AGGREGATE).strength(5.0f).sounds(BlockSoundGroup.STONE));

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

    public static Block ITEM_DUCT;
    public static Block IRON_PIPE;
    public static Block PUMP;
    public static Block TROMMEL;
    public static Block TROMMEL_CENTRE;
    public static Block FLUID_DRAIN;

    public static FlowableFluid FLOWING_BLOOD;
    public static FlowableFluid STILL_BLOOD;
    public static Item BLOOD_BUCKET;

    public static Block registerBlock(String id, Block block)
    {
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, id), block);
    }

    public static Block registerBlock(NMBlock block)
    {
        if (!(block instanceof Block))
        {
            throw new IllegalStateException("tried to register something that wasn't a block.");
        }
        return Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, block.getRegistryName()), (Block) block);
    }

    public static void registerBlocks()
    {


        // --- Building Blocks ---
        RUSTY_VENT = registerBlock(new BaseColumnBlock("rusty_vent", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));
        MESH_PANE = registerBlock(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        RUSTED_BARS = registerBlock(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        CAUTION_BLOCK = registerBlock(new BaseBlock("caution_block", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));

        SLOPE_TEST = registerBlock(new BaseStairsBlock(CAUTION_BLOCK.getDefaultState(), "slope_test", 64, FabricBlockSettings.of(Material.METAL).nonOpaque()));

        // --- Scaffolding
//        SCAFFOLD_PLATFORM = registerBlock(new MetalScaffoldingBlock("rusted_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        SCAFFOLD_STAIRS = registerBlock(new MetalScaffoldingStairs("rusted_metal_scaffold_stairs", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        BLUE_SCAFFOLD = registerBlock(new MetalScaffoldingBlock("blue_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        BLUE_SCAFFOLD_STAIRS = registerBlock(new MetalScaffoldingStairs("blue_metal_scaffold_stairs", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        YELLOW_SCAFFOLD = registerBlock(new MetalScaffoldingBlock("yellow_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
//        YELLOW_SCAFFOLD_STAIRS = registerBlock(new MetalScaffoldingStairs("yellow_metal_scaffold_stairs", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

        SCAFFOLD_TRAPDOOR = registerBlock(new ScaffoldTrapdoorBlock("rusted_metal_scaffold_trapdoor", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE).nonOpaque()));

        // --- General Blocks ---
        PIPE = registerBlock(new FluidPipeBlock("pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        IRON_PIPE = registerBlock(new PipeBlock("iron_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        TROMMEL = registerBlock(new TrommelBlock("trommel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f)));
        TROMMEL_CENTRE = registerBlock(new BaseDummyBlock("trommel_centre", FabricBlockSettings.of(Material.METAL).strength(4.0f)));

        // --- Fluid Transfer ---
        PUMP = registerBlock(new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        TANK = registerBlock(new TankBlock("basic_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        ITEM_BUFFER = registerBlock(new ItemBufferBlock("item_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
        GLASS_TANK = registerBlock(new GlassTankBlock("basic_glass_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        FLUID_METER = registerBlock(new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        FLUID_PORT = registerBlock(new FluidPortBlock("fluid_port", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        FLUID_DRAIN = registerBlock(new FluidDrainBlock("fluid_drain", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

        // --- Item Transfer ---
        ITEM_DUCT = registerBlock(new ItemDuctBlock("item_duct", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));

        // --- Fluids ---
        STILL_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "blood"), new BloodFluid.Still());
        FLOWING_BLOOD = Registry.register(Registry.FLUID, new Identifier(NeepMeat.NAMESPACE, "flowing_blood"), new BloodFluid.Flowing());
        BLOOD_BUCKET = Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, "fluid_hose"),
                new FluidHoseItem(STILL_BLOOD, new Item.Settings().maxCount(1).maxDamage(16).maxDamageIfAbsent(16)));

        TEST = Registry.register(Registry.BLOCK, new Identifier(NeepMeat.NAMESPACE, "acid"), new FluidBlock(STILL_BLOOD, FabricBlockSettings.copy(Blocks.WATER)){});

        for (NMBlock block : BLOCKS.values())
        {
            registerBlock(block.getRegistryName(), (Block) block);
        }

    }


}
