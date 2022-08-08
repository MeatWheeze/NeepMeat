package com.neep.neepmeat.fluid.ore_fat;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.BaseBucketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class OreFatBucketItem extends BaseBucketItem
{
    public OreFatBucketItem(String namespace, String registryName, Fluid fluid, Settings settings)
    {
        super(namespace, registryName, fluid, settings);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        tooltip.add(new TranslatableText(".lore").formatted(Formatting.GRAY));
    }

}
