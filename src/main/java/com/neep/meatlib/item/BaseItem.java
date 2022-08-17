package com.neep.meatlib.item;

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
    private final int lore;

    public BaseItem(String registryName, int loreLines, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.lore = loreLines;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        for (int i = 0; i < lore; ++i)
        {
            tooltip.add(new TranslatableText(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
        }
    }

}
