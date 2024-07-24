package com.neep.meatlib.registry;

import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.meatlib.block.BaseLeavesBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.*;

public class BlockRegistry
{
    public static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();

//    public static <T extends Block & MeatlibBlock> T queue(T block)
//    {
//        MeatLib.assertActive(block);
//        if (block == null)
//        {
//            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
//        }
//
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), block);
//        return block;
//    }

//    public static Block queue(MeatlibBlock block)
//    {
//        MeatLib.assertActive(block);
//        if (!(block instanceof Block))
//        {
//            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
//        }
//
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), (Block) block);
//        return (Block) block;
//    }

//    public static <T extends Block> T queue(T block, String registryName)
//    {
//        MeatLib.assertActive(block);
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
//        return block;
//    }

//    public static <T extends Block> T queueWithItem(T block, String registryName, ItemSettings itemSettings)
//    {
//        MeatLib.assertActive(block);
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
//        itemSettings.getFactory().create(block, ctx, itemSettings);
//        return block;
//    }

//    public static <T extends Block & MeatlibBlock> T queueWithItem(T block, ItemSettings itemSettings)
//    {
//        MeatLib.assertActive(block);
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), block);
//        itemSettings.getFactory().create(block, ctx, itemSettings);
//        return block;
//    }

//    public static <T extends Block> T queueWithItem(T block, String registryName)
//    {
//        MeatLib.assertActive(block);
//        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
//        ItemRegistry.queue(new BaseBlockItem(block, registryName, ItemSettings.block()));
//        return block;
//    }

//    public static void flush()
//    {
//        for (Map.Entry<Identifier, Block> entry : BLOCKS.entrySet())
//        {
//            Registry.register(Registries.BLOCK, entry.getKey(), entry.getValue());
//
//            REGISTERED_BLOCKS.add(entry.getValue());
//        }
//        BLOCKS.clear();
//    }

    public static BaseColumnBlock createLogBlock(RegistrationContext ctx, TooltipSupplier tooltipSupplier)
    {
        return new BaseColumnBlock(ctx, ItemSettings.block(), MeatlibBlockSettings.create().tags(Set.of(BlockTags.AXE_MINEABLE, BlockTags.LOGS)).strength(2.0f).sounds(BlockSoundGroup.WOOD));
    }

    public static BaseLeavesBlock createLeavesBlock(RegistrationContext ctx, BlockSoundGroup soundGroup)
    {
        return new BaseLeavesBlock(ctx, MeatlibBlockSettings.create()
                .tags(Set.of(FabricMineableTags.SHEARS_MINEABLE, BlockTags.LEAVES))
                .strength(0.2f)
                .ticksRandomly()
                .sounds(soundGroup)
                .nonOpaque()
                .allowsSpawning((p1, p2, p3, p4) -> false)
                .suffocates((p1, p2, p3) -> false).blockVision(((state, world, pos) -> false)))
        {
            @Override
            public ItemConvertible dropsLike()
            {
                return null;
            }
        };
    }
}
