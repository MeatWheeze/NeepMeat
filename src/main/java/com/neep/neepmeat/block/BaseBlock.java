package com.neep.neepmeat.block;

import com.neep.neepmeat.item.BaseBlockItem;
import net.minecraft.block.Block;

public class BaseBlock extends Block
{
    BaseBlockItem blockItem;

    public BaseBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
    }
}
