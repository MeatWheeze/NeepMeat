package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.meatlib.block.BaseLeavesBlock;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.ItemConvertible;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockRegistry
{
    public static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>();
    public static final Map<Identifier, Block> REGISTERED_BLOCKS = new LinkedHashMap<>();

    public static <T extends Block & MeatlibBlock> T queue(T block)
    {
        MeatLib.assertActive(block);
        if (block == null)
        {
            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), block);
        return block;
    }

    public static Block queue(MeatlibBlock block)
    {
        MeatLib.assertActive(block);
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), (Block) block);
        return (Block) block;
    }

    public static <T extends Block> T queue(T block, String registryName)
    {
        MeatLib.assertActive(block);
        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
        return block;
    }

    public static void flush()
    {
        for (Map.Entry<Identifier, Block> entry : BLOCKS.entrySet())
        {
            Registry.register(Registry.BLOCK, entry.getKey(), entry.getValue());

            REGISTERED_BLOCKS.put(entry.getKey(), entry.getValue());
        }
        BLOCKS.clear();
    }

    public static BaseColumnBlock createLogBlock(String name, TooltipSupplier tooltipSupplier)
    {
        return new BaseColumnBlock(name, ItemSettings.block(), FabricBlockSettings.of(Material.WOOD).strength(2.0f).sounds(BlockSoundGroup.WOOD))
        {
            @Override
            public TagKey<Block> getPreferredTool()
            {
                return BlockTags.AXE_MINEABLE;
            }
        };
    }

    public static BaseLeavesBlock createLeavesBlock(String name, BlockSoundGroup soundGroup)
    {
        return new BaseLeavesBlock(name, AbstractBlock.Settings.of(Material.LEAVES)
                .strength(0.2f)
                .ticksRandomly()
                .sounds(soundGroup)
                .nonOpaque()
                .allowsSpawning((p1, p2, p3, p4) -> false)
                .suffocates((p1, p2, p3) -> false).blockVision(((state, world, pos) -> false)))
        {
            @Override
            public TagKey<Block> getPreferredTool()
            {
                return FabricMineableTags.SHEARS_MINEABLE;
            }

            @Override
            public ItemConvertible dropsLike()
            {
                return null;
            }
        };
    }
}
