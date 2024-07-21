package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.BlockItem;

public class BaseTrapdoorBlock extends TrapdoorBlock implements MeatlibBlock
{
    public BaseBlockItem blockItem;

    public BaseTrapdoorBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings, BlockSetType blockSetType)
    {
        super(settings, blockSetType);
        this.blockItem = new BaseBlockItem(this, ctx, itemSettings);
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }
}
