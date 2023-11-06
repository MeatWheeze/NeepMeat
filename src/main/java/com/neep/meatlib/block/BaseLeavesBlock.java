package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;

public class BaseLeavesBlock extends LeavesBlock implements MeatlibBlock
{
    protected final String registryName;
    protected BlockItem blockItem;

    public BaseLeavesBlock(String name, Settings settings)
    {
        super(settings);
        this.registryName = name;
        this.blockItem = new BaseBlockItem(this, registryName, ItemSettings.block());
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
