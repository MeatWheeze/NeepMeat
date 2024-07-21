package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;

public class BaseLeavesBlock extends LeavesBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    public BaseLeavesBlock(RegistrationContext ctx, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, ctx, ItemSettings.block());
    }
}
