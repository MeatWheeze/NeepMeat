package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class BaseBlockItem extends BlockItem implements IMeatItem
{
    private final String name;
    private final TooltipSupplier tooltipSupplier;

    public BaseBlockItem(Block block, String registryName, ItemSettings itemSettings)
    {
        this(block, registryName, itemSettings, new FabricItemSettings().maxCount(itemSettings.maxCount));
    }

    public BaseBlockItem(Block block, String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(block, settings);
        this.name = registryName;
        this.tooltipSupplier = itemSettings.tooltipSupplier;
        ItemRegistry.queueItem(this);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltipSupplier.apply(this, tooltip);
//        for (int i = 0; i < loreLines; ++i)
//        {
//            tooltip.add(Text.translatable(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
//        }
    }

    public String getRegistryName()
    {
        return name;
    }
}
