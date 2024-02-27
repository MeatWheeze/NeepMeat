package com.neep.neepmeat.client.screen.tablet;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public interface GUIUtil
{
    static void renderBorder(DrawContext context, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);
        drawVerticalLine1(context, x + dx + offset, y - offset, y + dy + offset, col);
    }

    static void drawCenteredText(DrawContext context, TextRenderer textRenderer, Text text, float centerX, float y, int color, boolean shadow)
    {
//        OrderedText orderedText = text.asOrderedText();
        drawText(context, textRenderer, text, centerX - textRenderer.getWidth(text) / 2f, y, color, shadow);
    }


    static int drawText(DrawContext context, TextRenderer textRenderer, Text text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw( text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880 );
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, OrderedText text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw( text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880 );
        context.draw();
        return i;
    }

    static int drawText(DrawContext context, TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow)
    {
        int i = textRenderer.draw( text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880 );
        context.draw();
        return i;
    }

    static void drawHorizontalLine1(DrawContext context, int x1, int x2, int y, int color)
    {
        if (x2 < x1)
        {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        context.fill(x1, y, x2 + 1, y + 1, color);
    }

    static void drawVerticalLine1(DrawContext context, int x, int y1, int y2, int color)
    {
        if (y2 < y1)
        {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        context.fill(x, y1 + 1, x + 1, y2, color);
    }
}
