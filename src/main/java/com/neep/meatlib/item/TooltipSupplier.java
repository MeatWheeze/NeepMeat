package com.neep.meatlib.item;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

@FunctionalInterface
public interface TooltipSupplier
{
    static TooltipSupplier BLANK = (i, t) -> {};

    void apply(Item item, List<Text> tooltip);

    static TooltipSupplier blank()
    {
        return BLANK;
    }

    static TooltipSupplier simple(int lines)
    {
        return (item, list) ->
        {
            for (int lineNumber = 0; lineNumber < lines; ++lineNumber)
            {
                var txt = Text.translatable(item.getTranslationKey() + ".lore_" + lineNumber).formatted(Formatting.GRAY);
                wrapLines(list, txt);
            }
        };
    }

    static void wrapLines(List<Text> list, Text text)
    {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        List<OrderedText> newText = textRenderer.wrapLines(text, 200);

        for (var line : newText)
        {
            MutableText textLine = Text.empty();

            line.accept((index, style, codePoint) ->
            {
                textLine.append(Text.literal(Character.toString(codePoint)).setStyle(style));
                return true;
            });

            list.add(textLine);
        }
    }

    static TooltipSupplier hidden(int lines)
    {
        return new ShiftHidden(lines, Formatting.YELLOW);
    }

    static TooltipSupplier combine(TooltipSupplier supplier, TooltipSupplier supplier1)
    {
        return (item, tooltip) ->
        {
            supplier.apply(item, tooltip);
            supplier1.apply(item, tooltip);
        };
    }

    static void applyMessage(List<Text> tooltip)
    {
        tooltip.add(Text.translatable("message." + NeepMeat.NAMESPACE + ".shift_for_info").formatted(Formatting.GRAY));
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
                var txt = Text.translatable(item.getTranslationKey() + ".lore_0").formatted(Formatting.GRAY);
                wrapLines(tooltip, txt);
            }
            else
            {
                applyMessage(tooltip);
            }
        }
    }
}
