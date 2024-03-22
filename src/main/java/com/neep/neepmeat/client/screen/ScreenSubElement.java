package com.neep.neepmeat.client.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public void setFocused(@Nullable Element focused)
    {
//        if (getFocused() instanceof ClickableWidget widget)
//        {
//            widget.setFocused(false);
//        }
//
//        if (focused != null)
//        {
//            focused.setf(true);
//        }

        super.setFocused(focused);
    }

    //    protected final List<Element> children = Lists.newArrayList();

//    @Override
//    public List<? extends Element> children()
//    {
//        return children;
//    }


}
