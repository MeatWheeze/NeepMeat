package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
    protected final String registryName;
    protected int lore;

    public BaseSeedsItem(Block block, String registryName, int maxCount, int loreLines)
    {
        super(block, new FabricItemSettings().maxCount(maxCount));
        this.registryName = registryName;
        this.lore = loreLines;
        ItemRegistry.queueItem(this);
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
            tooltip.add(Text.translatable(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
//            tooltip.add(Text.translatable(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
        }
    }
}
