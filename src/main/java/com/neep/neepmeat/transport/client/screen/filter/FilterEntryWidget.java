package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterEntryWidget
{
    protected final List<ClickableWidget> children = new ArrayList<>();

    protected int x;
    protected int y;

    private final int w;
    private final int index;
    private int h;

    public FilterEntryWidget(int w, int index)
    {
        this.w = w;
        this.index = index;
        this.h = 40;
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
            c.setX(c.getX() + dx);
            c.setY(c.getY() + dy);
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
        GUIUtil.renderBorder(context, x + 2, y + 2, w - 5, h - 2, PLCCols.BORDER.col, -1);

        GUIUtil.renderBorder(context, x + 6, y + 6, 10, 3, PLCCols.BORDER.col, -1);
    }

    public int h()
    {
        return h;
    }
}
