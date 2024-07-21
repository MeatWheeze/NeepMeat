package com.neep.meatlib.item;

import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.NMItemGroups;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class BaseBlockItem extends BlockItem implements MeatlibItem
{
    private final TooltipSupplier tooltipSupplier;

    public BaseBlockItem(Block block, RegistrationContext ctx, ItemSettings itemSettings)
    {
        this(block, ctx, itemSettings, new MeatlibItemSettings().maxCount(itemSettings.maxCount).group(NMItemGroups.GENERAL));
    }

    public BaseBlockItem(Block block, RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(block, settings);
        this.tooltipSupplier = itemSettings.tooltipSupplier;
        ctx.append(block, this);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltipSupplier.apply(this, tooltip);
//        for (int i = 0; i < loreLines; ++i)
//        {
//            tooltip.add(new TranslatableText(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
//        }
    }

    public String getRegistryName()
    {
        return "";
    }
}
