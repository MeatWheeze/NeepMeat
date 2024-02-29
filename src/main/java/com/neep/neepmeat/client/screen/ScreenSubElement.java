package com.neep.neepmeat.client.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;

import java.util.List;

public abstract class ScreenSubElement extends AbstractParentElement implements Drawable, Element
{
    protected final List<Element> children = Lists.newArrayList();
    protected final List<Drawable> drawables = Lists.newArrayList();
    protected final List<Selectable> selectables = Lists.newArrayList();

    protected int x;
    protected int y;
    protected int screenWidth;
    protected int screenHeight;

    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final TextRenderer textRenderer = client.textRenderer;

    protected ScreenSubElement()
    {
    }

    public void setDimensions(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void init(int screenWidth, int screenHeight)
    {
        clearChildren();

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        init();
    }

    public void tick()
    {

    }

    protected abstract void init();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        for(Drawable drawable : this.drawables)
        {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<? extends Element> children()
    {
        return children;
    }

    protected void clearChildren()
    {
        this.drawables.clear();
        this.children.clear();
        this.selectables.clear();
    }

    public <T extends Drawable & Element & Selectable> void addDrawableChild(T t)
    {
        this.drawables.add(t);
        this.children.add(t);
        this.selectables.add(t);
    }

    public void addDrawable(Drawable t)
    {
        this.drawables.add(t);
    }
}
