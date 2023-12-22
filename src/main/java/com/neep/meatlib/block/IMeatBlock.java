package com.neep.meatlib.block;

import com.neep.neepmeat.datagen.tag.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

import java.util.List;

public interface IMeatBlock
{
    String getRegistryName();

    default List<Tag.Identified<Block>> getBlockTags()
    {
        return List.of(BlockTags.PICKAXE_MINEABLE);
    }

    default void addTags()
    {
        for (Tag.Identified<Block> id : getBlockTags())
        {
            BlockTagProvider.addToTag(id, (Block) this);
        }
    }

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem get(Block block, String name, int stack, boolean hasLore);
    }
}
