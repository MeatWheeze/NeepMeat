package com.neep.meatlib.block;

import net.minecraft.block.Block;

public class BaseDummyBlock extends Block implements NMBlock
{
    private final String registryName;

    public BaseDummyBlock(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
