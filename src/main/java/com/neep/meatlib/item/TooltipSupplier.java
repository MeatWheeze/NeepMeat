package com.neep.meatlib.item;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

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
                Text txt = new TranslatableText(item.getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY);
                list.add(txt);
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

    class TooltipText extends BaseText
    {
        @Override
        public BaseText copy()
        {
            return null;
        }
    }
}
