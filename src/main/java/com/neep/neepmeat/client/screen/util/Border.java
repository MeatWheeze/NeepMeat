package com.neep.neepmeat.client.screen.util;

import com.neep.neepmeat.api.plc.PLCCols;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import java.util.function.Supplier;

public class Border implements Rectangle, Drawable
{
    protected final int x;
    protected final int y;
    protected final int w;
    protected final int h;
    protected final int padding;
    protected final Supplier<Integer> col;

    public Border(int x, int y, int w, int h, int padding, Supplier<Integer> col)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.padding = padding;
        this.col = col;
    }

    public Border(Rectangle bounds, int padding, Supplier<Integer> col)
    {
        this( bounds.x(), bounds.y(), bounds.w(), bounds.h(), padding, col);
    }

    public Rectangle withoutPadding()
    {
        return new Immutable(x + padding, y + padding, w - padding * 2, h - padding * 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.renderBorder(context, x, y, w - 1, h - 1, col.get(), 0);
        GUIUtil.renderBorder(context, x, y, w - 1, h - 1, PLCCols.TRANSPARENT.col, -1);
    }

    @Override
    public int x()
    {
        return x;
    }

    @Override
    public int y()
    {
        return y;
    }

    @Override
    public int w()
    {
        return w;
    }

    @Override
    public int h()
    {
        return h;
    }

    public int padding()
    {
        return padding;
    }
}
