package com.neep.neepmeat.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class BaseStairsBlock extends StairsBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;

    protected BaseStairsBlock(BlockState baseBlockState, String blockName, int itemMaxStack, Settings settings)
    {
        super(baseBlockState, settings);
        this.registryName = blockName;
        this.maxStack = itemMaxStack;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
