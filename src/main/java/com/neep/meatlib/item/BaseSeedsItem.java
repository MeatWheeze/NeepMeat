package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.NMItemGroups;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BaseSeedsItem extends AliasedBlockItem implements MeatlibItem
{
    protected int lore;

    public BaseSeedsItem(Block block, RegistrationContext ctx, int maxCount, int loreLines)
    {
        super(block, new MeatlibItemSettings().maxCount(maxCount).group(NMItemGroups.GENERAL));
        this.lore = loreLines;
    }

    @Override
    public String getRegistryName()
    {
        return "";
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        for (int i = 0; i < lore; ++i)
        {
            tooltip.add(Text.translatable(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
//            tooltip.add(Text.translatable(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
        }
    }
}
