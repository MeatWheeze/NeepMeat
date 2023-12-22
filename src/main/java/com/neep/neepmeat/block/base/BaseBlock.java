package com.neep.neepmeat.block.base;

import com.neep.neepmeat.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BaseBlock extends Block
{
    public BaseBlockItem blockItem;

    public BaseBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }
}
