package com.neep.meatlib.item;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface TooltipSupplier
{
    LineBreakingVisitor VISITOR = new LineBreakingVisitor(30);

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
            for (int i = 0; i < lines; ++i)
            {
                var txt = Text.translatable(item.getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY);
//                Text txt = Text.translatable(item.getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY);
//                txt.visit(asString ->
//                {
//                    return Optional.empty();
//                });
                list.add(txt);
            }
        };
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
                for (int i = 0; i < lines; ++i)
                {
                    tooltip.add(Text.translatable(item.getTranslationKey() + ".lore_" + i).formatted(formatting));
                }
            }
            else
            {
                applyMessage(tooltip);
            }
        }
    }

    class LineBreakingVisitor
    {
        private final int maxWidth;
        private float totalWidth;
        private int lastSpaceBreak = -1;
        private Style lastSpaceStyle = Style.EMPTY;
        private int count;
        private int startOffset;

        protected String currentLine = "";

        public LineBreakingVisitor(int maxWidth)
        {
            this.maxWidth = maxWidth;
        }

        public boolean accept(int i, Style style, int codePoint, List<Text> tooltips)
        {
            int k = i + this.startOffset;
            switch (codePoint)
            {
                case 10:
                {
                    return this.breakLine(k, style, tooltips);
                }
                case 32:
                {
                    this.lastSpaceBreak = k;
                    this.lastSpaceStyle = style;
                }
            }
            this.totalWidth += 1;
            if (this.totalWidth > this.maxWidth)
            {
                if (this.lastSpaceBreak != -1)
                {
                    return this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle, tooltips);
                }
                return this.breakLine(k, style, tooltips);
            }
            this.count = k + Character.charCount(codePoint);
//            currentLine.append(Character.toString(codePoint));
            currentLine += Character.toString(codePoint);
            return true;
        }

        public void reset()
        {
            currentLine = "";
            totalWidth = 0;
        }

        private boolean breakLine(int finishIndex, Style finishStyle, List<Text> tooltips)
        {
            tooltips.add(Text.of(currentLine));
            reset();
            return true;
        }

        public void offset(int extraOffset)
        {
            this.startOffset += extraOffset;
        }
    }
}
