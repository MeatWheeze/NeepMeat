package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.neepmeat.api.processing.random_ores.RandomOres;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;

public class DebugItem extends BaseItem
{
    public DebugItem(Settings settings)
    {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        var provider = RandomOres.INSTANCE.makeProvider(context.getWorld(), context.getBlockPos(), Random.create());

        System.out.println(provider.print());

        return ActionResult.SUCCESS;
    }
}
