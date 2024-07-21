package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.BlockItem;

public class BasePaneBlock extends PaneBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    public BasePaneBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }
}
