package com.neep.neepmeat.client.screen.tablet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class ContentPane extends Screen implements Drawable, Selectable, Element, ParentElement
{
    protected int screenOffsetX = 5;
    protected int screenOffsetY = 5;
    protected int backgroundWidth = 255;
    protected int backgroundHeight = 194;
    protected int screenWidth = 156;
    protected int screenHeight = 145;

    protected int x;
    protected int y;

    protected final ITabletScreen parent;

    protected ContentPane(Text title, ITabletScreen parent)
    {
        super(title);
        this.parent = parent;
    }

    public void init(MinecraftClient client)
    {
        this.init(client, width, height);
    }

    public void setDimensions(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public SelectionType getType()
    {
//        if (this.focused)
//        {
//            return Selectable.SelectionType.FOCUSED;
//        }
//        if (this.hovered)
//        {
//            return Selectable.SelectionType.HOVERED;
//        }
//        return Selectable.SelectionType.NONE;
        return SelectionType.FOCUSED;
    }

    @Override
    public void init()
    {
        super.init();
    }
}