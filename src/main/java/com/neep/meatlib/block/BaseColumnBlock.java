package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;

public class BaseColumnBlock extends PillarBlock implements MeatlibBlock
{
    public final BlockItem blockItem;
    private final String regsitryName;

    public BaseColumnBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
        this.regsitryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return regsitryName;
    }

}
