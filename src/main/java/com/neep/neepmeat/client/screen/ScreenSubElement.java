package com.neep.neepmeat.client.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ScreenSubElement extends Screen implements Drawable, Element
{
    protected int x;
    protected int y;
    protected int screenWidth;
    protected int screenHeight;
    protected int elementWidth;
    protected int elementHeight;

    protected ScreenSubElement()
    {
        super(Text.empty());
    }

    public void setDimensions(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public boolean shouldCloseOnEsc()
    {
        return false;
    }

    @Override
    protected void init()
    {
        super.init();
        this.screenWidth = width;
        this.screenHeight = height;
    }

    //    protected final List<Element> children = Lists.newArrayList();

//    @Override
//    public List<? extends Element> children()
//    {
//        return children;
//    }


}
