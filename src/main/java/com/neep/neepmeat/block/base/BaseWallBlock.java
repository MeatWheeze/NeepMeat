package com.neep.neepmeat.block.base;

import net.minecraft.block.WallBlock;

public class BaseWallBlock extends WallBlock implements NMBlock
{
    protected String registryName;
    protected int maxStack;

    protected BaseWallBlock(String blockName, int itemMaxStack, Settings settings)
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
