package com.neep.neepmeat.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;

public class BaseSlabBlock extends SlabBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;

    protected BaseSlabBlock(BlockState baseBlockState, String blockName, int itemMaxStack, Settings settings)
    {
        super(settings);
        this.registryName = blockName;
        this.maxStack = itemMaxStack;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
