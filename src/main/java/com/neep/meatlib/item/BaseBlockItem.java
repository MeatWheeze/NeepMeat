package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
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
    private final boolean hasLore;

    public BaseBlockItem(Block block, String registryName, int itemMaxStack, boolean hasLore)
    {
        super(block, new FabricItemSettings().maxCount(itemMaxStack).group(NMItemGroups.GENERAL));
        this.name = registryName;
        this.hasLore = hasLore;
        ItemRegistry.queueItem(this);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (hasLore)
        {
            tooltip.add(new TranslatableText("item." + NeepMeat.NAMESPACE + "." + name + ".lore").formatted(Formatting.GRAY));
        }
    }

    public String getRegistryName()
    {
        return name;
    }
}
