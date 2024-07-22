package com.neep.meatlib.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class BaseItem extends Item implements MeatlibItem
{
    private final TooltipSupplier tooltipSupplier;

    public BaseItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(settings);
        this.tooltipSupplier = tooltipSupplier;
    }
    public BaseItem(Settings settings)
    {
        super(settings);
        this.tooltipSupplier = TooltipSupplier.blank();
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltipSupplier.apply(this, tooltip);
    }
}
