package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;

public class BaseStairsBlock extends StairsBlock implements MeatlibBlock
{
    protected String registryName;
    protected BlockItem blockItem;

    public BaseStairsBlock(BlockState baseBlockState, String blockName, ItemSettings itemSettings, Settings settings)
    {
        super(baseBlockState, settings);
        this.registryName = blockName;
        this.blockItem = itemSettings.create(this, blockName, itemSettings);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
