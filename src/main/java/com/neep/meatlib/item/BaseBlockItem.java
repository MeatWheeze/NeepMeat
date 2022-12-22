package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BaseBlockItem extends BlockItem implements IMeatItem
{
    private final String name;
    private final int loreLines;

    public BaseBlockItem(Block block, String registryName, int itemMaxStack, boolean hasLore)
    {
        super(block, new FabricItemSettings().maxCount(itemMaxStack).group(NMItemGroups.GENERAL));
        this.name = registryName;
        this.loreLines = hasLore ? 1 : 0;
        ItemRegistry.queueItem(this);
    }

    public BaseBlockItem(Block block, String registryName, int itemMaxStack, int loreLines)
    {
        super(block, new FabricItemSettings().maxCount(itemMaxStack).group(NMItemGroups.GENERAL));
        this.name = registryName;
        this.loreLines = loreLines;
        ItemRegistry.queueItem(this);
    }

    public BaseBlockItem(Block block, String registryName, int itemMaxStack, int loreLines, Settings settings)
    {
        super(block, settings);
        this.name = registryName;
        this.loreLines = loreLines;
        ItemRegistry.queueItem(this);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        for (int i = 0; i < loreLines; ++i)
        {
            tooltip.add(new TranslatableText(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
        }
    }

    public String getRegistryName()
    {
        return name;
    }
}
