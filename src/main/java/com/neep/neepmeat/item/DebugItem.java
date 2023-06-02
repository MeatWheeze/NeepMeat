package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class DebugItem extends BaseItem
{
    public DebugItem(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        System.out.println(context.getWorld().getBlockEntity(context.getBlockPos()));
        return ActionResult.SUCCESS;
    }
}
