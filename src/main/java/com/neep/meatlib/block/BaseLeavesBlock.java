package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;

public class BaseLeavesBlock extends LeavesBlock implements IMeatBlock
{
    protected final String registryName;
    protected BlockItem blockItem;

    public BaseLeavesBlock(String name, Settings settings)
    {
        super(settings);
        this.registryName = name;
        this.blockItem = new BaseBlockItem(this, registryName, 64, 0);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
