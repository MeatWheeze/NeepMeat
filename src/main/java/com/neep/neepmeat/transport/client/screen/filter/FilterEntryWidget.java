package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Point;
import com.neep.neepmeat.item.filter.Filter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterEntryWidget<T extends Filter>
{
    protected final List<ClickableWidget> children = new ArrayList<>();
    protected final T filter;

    protected int x;
    protected int y;

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

    public void init()
    {
    }

    protected void addWWidget(ClickableWidget widget)
    {
        children.add(widget);
    }

    public void setPos(int x, int y)
    {
        int dx = x - this.x;
        int dy = y - this.y;
        this.x = x;
        this.y = y;

        children.forEach(c ->
        {
            c.setPos(c.x() + dx, c.y() + dy);
        });
    }

    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        for (var widget : children)
        {
            widget.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void render(DrawContext context, double mouseX, double mouseY, float delta)
    {
        GUIUtil.renderBorderInner(context, x, y, w, h, PLCCols.BORDER.col, 0);

        for (var widget : children)
        {
            widget.render(context, (int) mouseX, (int) mouseY, delta);
        }
    }

    protected void updateToServer()
    {
        handler.updateToServer.emitter().apply(index, filter.writeNbt(new NbtCompound()));
    }

    public int h()
    {
        return h;
    }
}
