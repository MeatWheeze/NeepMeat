package com.neep.meatlib.item;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import javax.tools.Tool;
import java.util.List;

@FunctionalInterface
public interface TooltipSupplier
{
    void apply(Item item, List<Text> tooltip);

    static TooltipSupplier empty()
    {
        return (i, t) -> {};
    }

    static TooltipSupplier simple(int lines)
    {
        return (item, list) ->
        {
            for (int i = 0; i < lines; ++i)
            {
                list.add(new TranslatableText(item.getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
            }
        };
    }

    static TooltipSupplier hidden(int lines)
    {
        return new ShiftHidden(lines, Formatting.YELLOW);
    }

    static void applyMessage(List<Text> tooltip)
    {
        tooltip.add(new TranslatableText("message." + NeepMeat.NAMESPACE + ".shift_for_info").formatted(Formatting.GRAY));
    }

    class ShiftHidden implements TooltipSupplier
    {
        private final int lines;
        private final Formatting formatting;

        public ShiftHidden(int lines, Formatting formatting)
        {
            this.lines = lines;
            this.formatting = formatting;
        }

        @Override
        public void apply(Item item, List<Text> tooltip)
        {
            if (lines != 0 && Screen.hasShiftDown())
            {
                for (int i = 0; i < lines; ++i)
                {
                    tooltip.add(new TranslatableText(item.getTranslationKey() + ".lore_" + i).formatted(formatting));
                }
            }
            else
            {
                applyMessage(tooltip);
            }
        }
    }
}
