package com.neep.neepmeat.block.base;

import com.neep.neepmeat.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BaseBlock extends Block implements NMBlock
{
    public BaseBlockItem blockItem;
    private String registryName;

    public BaseBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
        this.registryName = itemName;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
