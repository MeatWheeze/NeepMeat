package com.neep.neepmeat.block.base;

import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BaseBlock extends Block implements NMBlock
{
    public BaseBlockItem blockItem;
    private String registryName;

    public BaseBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, registryName, itemMaxStack, hasLore);
        this.registryName = registryName;
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
