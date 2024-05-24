package com.neep.neepmeat.client.screen.util;

import com.neep.neepmeat.api.plc.PLCCols;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Supplier;

import static com.neep.neepmeat.client.screen.tablet.GUIUtil.drawHorizontalLine1;
import static com.neep.neepmeat.client.screen.tablet.GUIUtil.drawVerticalLine1;

public class BorderScrollRight extends Border
{
    public BorderScrollRight(int x, int y, int w, int h, int padding, Supplier<Integer> col)
    {
        super(x, y, w, h, padding, col);
    }

    public BorderScrollRight(Rectangle bounds, int padding, Supplier<Integer> col)
    {
        super(bounds, padding, col);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        renderBorder(context, x, y, w - 1, h - 1, col.get(), 0);
        renderBorder(context, x, y, w - 1, h - 1, PLCCols.TRANSPARENT.col, -1);

        drawVerticalLine1(context, x + w - 1, y + 1, y + h, PLCCols.TRANSPARENT.col);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta, float scrollAmount)
    {
        render(context, mouseX, mouseY, delta);

        float barHeight = 10;
        float barTravel = h - 4 - barHeight - 1;

        int yOff = (int) (y + 2 + scrollAmount * barTravel);
        drawVerticalLine1(context, x + w - 1, yOff, (int) (yOff + barHeight), col.get());
    }

    protected void renderBorder(DrawContext context, int x, int y, int dx, int dy, int col, int offset)
    {
        drawHorizontalLine1(context, x - offset, x + dx + offset, y - offset, col);
        drawVerticalLine1(context, x - offset, y - offset, y + dy + offset, col);
        drawHorizontalLine1(context, x - offset, x + dx + offset, y + dy + offset, col);

        drawVerticalLine1(context, x + dx + offset, y - offset, y + 2, col);
        drawVerticalLine1(context, x + dx + offset, y - offset + dy - 2, y + offset + dy, col);
    }
}
