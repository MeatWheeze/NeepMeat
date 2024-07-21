package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;

public class BaseColumnBlock extends PillarBlock implements MeatlibBlock, MeatlibBlockExtension
{
    public final BlockItem blockItem;

    public BaseColumnBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }
}
