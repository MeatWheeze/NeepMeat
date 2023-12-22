package com.neep.neepmeat.init;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.*;
import com.neep.neepmeat.block.base.BaseBuildingBlock;
import com.neep.neepmeat.block.base.BasePaneBlock;
import com.neep.neepmeat.block.FluidPipeBlock;
import com.neep.neepmeat.block.base.NMBlock;
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
    public static Block SCAFFOLD_PLATFORM;
    public static Block SCAFFOLD_STAIRS;
    public static Block RUSTED_BARS;

    public static Block POLISHED_IRON_BRICKS = new BaseBuildingBlock("rusted_iron_bricks", 64, false, FabricBlockSettings.of(Material.METAL).strength(5.0f).sounds(BlockSoundGroup.NETHERITE));

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
        MESH_PANE = registerBlock(new BasePaneBlock("mesh_panel", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        RUSTED_BARS = registerBlock(new BasePaneBlock("rusted_bars", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        SCAFFOLD_PLATFORM = registerBlock(new MetalScaffoldingBlock("rusted_metal_scaffold", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        SCAFFOLD_STAIRS = registerBlock(new MetalScaffoldingStairs("rusted_metal_scaffold_stairs", 64, false, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

        // --- General Blocks ---
        PIPE = registerBlock(new FluidPipeBlock("pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        IRON_PIPE = registerBlock(new PipeBlock("iron_pipe", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));

        // --- Transfer ---
        PUMP = registerBlock(new PumpBlock("pump", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        TANK = registerBlock(new TankBlock("basic_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        ITEM_BUFFER = registerBlock(new ItemBufferBlock("item_buffer", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.METAL)));
        GLASS_TANK = registerBlock(new GlassTankBlock("basic_glass_tank", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
        FLUID_METER = registerBlock(new FluidMeter("fluid_meter", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.LANTERN)));
        FLUID_PORT = registerBlock(new FluidPortBlock("fluid_port", 64, true, FabricBlockSettings.of(Material.METAL).strength(4.0f).sounds(BlockSoundGroup.NETHERITE)));
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
