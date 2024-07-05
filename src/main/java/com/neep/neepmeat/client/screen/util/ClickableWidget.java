package com.neep.neepmeat.client.screen.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public abstract class ClickableWidget extends net.minecraft.client.gui.widget.ClickableWidget implements Point.Mutable, Rectangle
{
    protected final MinecraftClient client = MinecraftClient.getInstance();

    public ClickableWidget(int x, int y, int width, int height, Text message)
    {
        super(x, y, width, height, message);
    }

    @Override
    public int x()
    {
        return getX();
    }

    @Override
    public int y()
    {
        return getY();
    }

    @Override
    public int w()
    {
        return width;
    }

    @Override
    public int h()
    {
        return height;
    }

    @Override
    public void setPos(int x, int y)
    {
        setX(x);
        setY(y);
    }
}
