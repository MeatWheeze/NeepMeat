package com.neep.meatlib.item;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BaseItem extends Item implements IMeatItem
{
    private final String registryName;
    private final boolean hasLore;

    public BaseItem(String registryName, boolean hasLore, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.hasLore = hasLore;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        if (hasLore)
        {
            tooltip.add(new TranslatableText("item." + NeepMeat.NAMESPACE + "." + getRegistryName() + ".lore").formatted(Formatting.GRAY));
        }
    }

}
