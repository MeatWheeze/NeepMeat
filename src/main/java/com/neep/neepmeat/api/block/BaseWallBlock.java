package com.neep.neepmeat.api.block;

import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;

public class BaseWallBlock extends WallBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;
    protected BlockItem blockItem;

    protected BaseWallBlock(String blockName, int itemMaxStack, Settings settings)
    {
        super(settings);
        this.registryName = blockName;
        this.maxStack = itemMaxStack;
        this.blockItem = new BaseBlockItem(this, blockName, itemMaxStack, false);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
