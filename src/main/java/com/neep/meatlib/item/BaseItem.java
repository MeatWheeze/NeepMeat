package com.neep.meatlib.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseItem extends Item implements IMeatItem
{
    private final String registryName;
    private final TooltipSupplier tooltipSupplier;
    private ItemGroup group = null;

    public BaseItem(final String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.tooltipSupplier = tooltipSupplier;
    }
    public BaseItem(final String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.tooltipSupplier = TooltipSupplier.blank();
    }

    public BaseItem group(ItemGroup group)
    {
        MeatItemGroups.queueItem(group, this);
        this.group = group;
        return this;
    }

    @Override
    @Nullable
    public ItemGroup getGroupOverride()
    {
        return this.group;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltipSupplier.apply(this, tooltip);
    }
}
