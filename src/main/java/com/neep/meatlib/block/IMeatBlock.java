package com.neep.meatlib.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public interface IMeatBlock
{
    String getRegistryName();

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem get(Block block, String name, int stack, boolean hasLore);
    }
}
