package com.neep.meatlib.block;

import com.neep.neepmeat.datagen.tag.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;

import java.util.List;

public interface IMeatBlock
{
    String getRegistryName();

    default List<TagKey<Block>> getBlockTags()
    {
        return List.of(BlockTags.PICKAXE_MINEABLE);
    }

    default void addTags()
    {
        for (TagKey<Block> id : getBlockTags())
        {
            BlockTagProvider.addToTag(id, (Block) this);
        }
    }

    default boolean dropsSelf()
    {
        return true;
    }

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem get(Block block, String name, int stack, boolean hasLore);
    }
}
