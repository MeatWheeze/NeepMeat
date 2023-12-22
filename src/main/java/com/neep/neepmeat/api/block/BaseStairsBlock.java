package com.neep.neepmeat.api.block;

import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;

public class BaseStairsBlock extends StairsBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;
    protected BlockItem blockItem;

    public BaseStairsBlock(BlockState baseBlockState, String blockName, int itemMaxStack, Settings settings)
    {
        super(baseBlockState, settings);
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
