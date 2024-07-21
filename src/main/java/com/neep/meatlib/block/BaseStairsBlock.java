package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;

public class BaseStairsBlock extends StairsBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    public BaseStairsBlock(RegistrationContext ctx, BlockState baseBlockState, ItemSettings itemSettings, Settings settings)
    {
        super(baseBlockState, settings);
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }
}
