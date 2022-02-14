package com.neep.neepmeat.block.base;

import com.neep.neepmeat.item.BaseBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;

public class BaseSlabBlock extends SlabBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;
    protected BlockItem blockItem;

    protected BaseSlabBlock(BlockState baseBlockState, String blockName, int itemMaxStack, Settings settings)
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
