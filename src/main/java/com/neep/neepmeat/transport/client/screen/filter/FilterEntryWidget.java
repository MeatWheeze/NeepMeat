package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Point;
import com.neep.neepmeat.item.filter.Filter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterEntryWidget<T extends Filter> extends ScreenSubElement implements Point.Mutable
{
    protected final List<ClickableWidget> positionables = new ArrayList<>();
    protected T filter;

    protected final int index;
    protected final FilterScreenHandler handler;

    protected int h;
    protected final int w;

    public FilterEntryWidget(int w, int h, int index, T filter, FilterScreenHandler handler)
    {
        this.w = w;
        this.h = h;
        this.index = index;
        this.filter = filter;
        this.handler = handler;
    }

    @Override
    protected void clearChildren()
    {
        super.clearChildren();
        positionables.clear();
    }

    public void init()
    {
    }

    @Override
    public void setPos(int x, int y)
    {
        int dx = x - this.x;
        int dy = y - this.y;
        this.x = x;
        this.y = y;

        positionables.forEach(c ->
        {
            c.setPos(c.x() + dx, c.y() + dy);
        });
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isMouseOver(mouseX, mouseY))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public <V extends Drawable & Element & Selectable> void addDrawableChild(V t)
    {
        super.addDrawableChild(t);
        if (t instanceof ClickableWidget clickableWidget)
        {
            positionables.add(clickableWidget);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.renderBorderInner(context, x, y, w, h, PLCCols.BORDER.col, 0);

        super.render(context, mouseX, mouseY, delta);
    }

    protected void updateToServer()
    {
        handler.updateToServer.emitter().apply(index, filter.writeNbt(new NbtCompound()));
    }

    public int h()
    {
        return h;
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

    public void updateFilter(Filter filter)
    {
        // EEEEEK
        this.filter = (T) filter;
    }

    protected T getFilter()
    {
        return filter;
    }
}
