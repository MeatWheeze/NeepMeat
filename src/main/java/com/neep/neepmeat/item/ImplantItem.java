package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ImplantItem extends BaseItem
{
    public ImplantItem(String registryName, int lines, Settings settings)
    {
        super(registryName, new ImplantTooltipSupplier(registryName, lines), settings);
        ItemRegistry.queueItem(this);
    }

    public static class ImplantTooltipSupplier implements TooltipSupplier
    {
        private final String name;
        private final int lines;
        public ImplantTooltipSupplier(String name, int lines)
        {
            this.name = name;
            this.lines = lines;
        }

        @Override
        public void apply(Item item, List<Text> tooltip)
        {
            tooltip.add(Text.translatable("message." + NeepMeat.NAMESPACE + ".implant.title").formatted(Formatting.BOLD).formatted(Formatting.GOLD));
            for (int i = 0; i < lines; ++i)
            {
                var txt = Text.translatable("implant." + NeepMeat.NAMESPACE + "." + name + ".desc_" + i).formatted(Formatting.GOLD);
                tooltip.add(txt);
            }
        }
    }
}
