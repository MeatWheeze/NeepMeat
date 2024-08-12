package com.neep.neepmeat.client.screen.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

import java.util.function.Supplier;

public class Background implements Drawable
{
    private final int padding;
    private int x;
    private int y;
    private int w;
    private int h;

    // The padding here is used in a completely different way to all the other borders I implemented.
    // This is because I'm an idiot.
    // Hopefully this is the correct way.
    public Background(int x, int y, int w, int h, int padding)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.padding = padding;
    }

    public Background(Rectangle bounds, int padding, Supplier<Integer> col)
    {
        this(bounds.x(), bounds.y(), bounds.w(), bounds.h(), padding);
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setW(int w)
    {
        this.w = w;
    }

    public void setH(int h)
    {
        this.h = h;
    }

    public Rectangle withoutPadding()
    {
        return new Rectangle.Immutable(x, y, w, h);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.drawScreenBorder(context, x - padding, y - padding, w + 2 * padding, h + 2 * padding);
    }

    public int padding()
    {
        return padding;
    }
}
