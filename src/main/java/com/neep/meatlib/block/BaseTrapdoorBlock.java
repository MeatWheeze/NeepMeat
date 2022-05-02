package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.BlockItem;

public class BaseTrapdoorBlock extends TrapdoorBlock implements NMBlock
{
    public BaseBlockItem blockItem;
    private final String registryName;

    public BaseTrapdoorBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
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
