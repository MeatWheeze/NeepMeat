package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class FluidComponentItem extends BaseBlockItem
{
    public FluidComponentItem(Block block, String registryName, int itemMaxStack, boolean hasLore)
    {
        super(block, registryName, itemMaxStack, hasLore);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (!actionResult.isAccepted() && this.isFood())
        {
            ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2;
        }
        return actionResult;
    }
}