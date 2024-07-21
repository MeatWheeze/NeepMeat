package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.item.BlockItem;

public class BaseGlassBlock extends AbstractGlassBlock implements MeatlibBlock
{
    public BlockItem blockItem;
    private String registryName;

    public BaseGlassBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
        this.registryName = registryName;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }
}
