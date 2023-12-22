package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

import java.util.List;

public interface MeatlibBlock extends ItemConvertible
{
    String getRegistryName();

    default List<TagKey<Block>> getBlockTags()
    {
        return List.of(BlockTags.PICKAXE_MINEABLE);
    }

    static void validate(MeatlibBlock meatBlock)
    {
        if (!(meatBlock instanceof Block)) throw new IllegalStateException("IMeatBlock must only be implemented by blocks");
    }

    default void addTags()
    {
        for (TagKey<Block> id : getBlockTags())
        {
//            BlockTagProvider.addToTag(id, (Block) this);
        }
    }

    default TagKey<Block> getPreferredTool()
    {
        return BlockTags.PICKAXE_MINEABLE;
    }

    default boolean autoGenDrop()
    {
        return true;
    }

    default ItemConvertible dropsLike()
    {
        validate(this);
        return (Block) this;
    }

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem create(Block block, String name, ItemSettings settings);
    }
}
