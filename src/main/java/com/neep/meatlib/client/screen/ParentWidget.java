package com.neep.meatlib.client.screen;

import com.neep.neepmeat.client.screen.util.ClickableWidget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

import java.util.List;

public abstract class ParentWidget extends AbstractParentElement implements ClickableWidget, Selectable
{
    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final TextRenderer textRenderer = client.textRenderer;
    private final List<ClickableWidget> positionables = new ObjectArrayList<>();
    private final List<Drawable> drawables = new ObjectArrayList<>();
    protected int x, y;
    protected int w, h;

    public ParentWidget(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public List<ClickableWidget> children()
    {
        return positionables;
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

    public void init()
    {
        positionables.clear();
        drawables.clear();
    }

    public void tick()
    {

    }

    protected <T extends ClickableWidget> T addChild(T widget)
    {
        positionables.add(widget);
        return widget;
    }

    protected <T extends Drawable> T addChild(T widget)
    {
        drawables.add(widget);
        return widget;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        children().forEach(clickableWidget -> clickableWidget.render(context, mouseX, mouseY, delta));
    }

    @Override
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
        init();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }

    @Override
    public SelectionType getType()
    {
//        return SelectionType.NONE; // Not sure what this should really return
        if (this.isFocused())
        {
            return Selectable.SelectionType.FOCUSED;
        }
        else
        {
//            return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
            return SelectionType.NONE;
        }
    }
}
