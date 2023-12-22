package com.neep.neepmeat.client.screen.tablet;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class GUIUtil
{
    public static void renderBorder(MatrixStack matrices, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(matrices, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(matrices, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(matrices, x - offset, x + dx + offset, y + dy + offset, col);
        drawVerticalLine1(matrices, x + dx + offset, y - offset, y + dy + offset, col);
    }

    protected static void drawHorizontalLine1(MatrixStack matrices, int x1, int x2, int y, int color)
    {
        if (x2 < x1)
        {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        DrawableHelper.fill(matrices, x1, y, x2 + 1, y + 1, color);
    }

    protected static void drawVerticalLine1(MatrixStack matrices, int x, int y1, int y2, int color)
    {
        if (y2 < y1)
        {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        DrawableHelper.fill(matrices, x, y1 + 1, x + 1, y2, color);
    }
}
