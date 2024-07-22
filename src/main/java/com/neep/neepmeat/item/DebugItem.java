package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class DebugItem extends BaseItem
{
    public DebugItem(Settings settings)
    {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        System.out.println(context.getWorld().getBlockEntity(context.getBlockPos()));
        return ActionResult.SUCCESS;
    }
}
