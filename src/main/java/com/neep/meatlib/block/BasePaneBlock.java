package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.BlockItem;

public class BasePaneBlock extends PaneBlock implements IMeatBlock
{
    protected String registryName;
    protected int maxStack;
    protected BlockItem blockItem;

    public BasePaneBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.registryName = itemName;
        this.maxStack = itemMaxStack;
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
