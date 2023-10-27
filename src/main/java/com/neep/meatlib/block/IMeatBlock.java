package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.datagen.tag.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import java.util.List;

public interface IMeatBlock extends ItemConvertible
{
    String getRegistryName();

    default List<TagKey<Block>> getBlockTags()
    {
        return List.of(BlockTags.PICKAXE_MINEABLE);
    }

    static void validate(IMeatBlock meatBlock)
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
