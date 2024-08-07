package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.FallingBlock;

public class ActiveWasteBlock extends FallingBlock
{
    public ActiveWasteBlock(RegistrationContext ctx, Settings settings)
    {
        super(settings);
        ctx.appendItem(this, new BaseBlockItem(this, ctx, ItemSettings.block()));
    }
}
