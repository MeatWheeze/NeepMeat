package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.item.BlockItem;

public class BaseSaplingBlock extends SaplingBlock implements IMeatBlock
{
    protected final String registryName;
    protected BlockItem blockItem;

    public BaseSaplingBlock(String registryName, SaplingGenerator generator, int loreLines, Settings settings)
    {
        super(generator, settings);
        this.registryName = registryName;
        this.blockItem = new BaseBlockItem(this, registryName, 64, loreLines);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
