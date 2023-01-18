package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.BlockItem;

public class BasePaneBlock extends PaneBlock implements IMeatBlock
{
    protected String registryName;
    protected BlockItem blockItem;

    public BasePaneBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
