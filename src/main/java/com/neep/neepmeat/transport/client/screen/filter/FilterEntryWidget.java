package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.util.*;
import com.neep.neepmeat.item.filter.Filter;
import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class FilterEntryWidget<T extends Filter> extends ScreenSubElement implements Point.Mutable, Rectangle
{
    protected final List<ClickableWidget> positionables = new ArrayList<>();
    protected boolean focused;

    private final Text name;
    protected FilterList.Entry entry;
    protected T filter;

    protected final int index;
    protected final FilterScreenHandler handler;

    protected int h;
    protected final int w;

    public FilterEntryWidget(int w, int h, int index, Text name, FilterList.Entry entry, T filter, FilterScreenHandler handler)
    {
        this.w = w;
        this.h = h;
        this.index = index;
        this.name = name;
        this.entry = entry;
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
        addDrawableChild(new InvertButtonWidget(x2() - 10 - 2, y() + 3,
                11, 11,
                () -> entry.getInverted(),
                Text.of("Invert"),
                (button, toggled) -> handler.setInverted(index, toggled)));
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
            boolean sub = super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return isWithin(mouseX, mouseY) || super.isMouseOver(mouseX, mouseY);
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
        int col = isFocused() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
        GUIUtil.renderBorderInner(context, x, y, w, h, col, 0);

        GUIUtil.drawText(context, textRenderer, name, x() + 3, y() + 3, PLCCols.TEXT.col, false);

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

    @Override
    public int w()
    {
        return w;
    }

    public void updateFilter(FilterList.Entry entry)
    {
        // EEEEEK
        this.entry = entry;
        this.filter = (T) entry.getFilter();
    }

    protected T getFilter()
    {
        return filter;
    }

    @Override
    public void setFocused(boolean focused)
    {
        this.focused = focused;
        super.setFocused(focused);
        if (!focused)
        {
            setFocused(null);
//            children.forEach(c -> c.setFocused(false));
        }
    }

    @Override
    public boolean isFocused()
    {
        return focused || super.isFocused();
    }

    public static class InvertButtonWidget extends CheckboxWidget
    {
        public InvertButtonWidget(int x, int y, int w, int h, BooleanSupplier toggled, Text message, ToggleAction onToggle)
        {
            super(x, y, w, h, toggled, message, onToggle);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            String box = isToggled() ? "☒" : "☐";
            int borderCol = borderActive(mouseX, mouseY) ? PLCCols.SELECTED.col : PLCCols.BORDER.col;

            int boxWidth = textRenderer.getWidth(box);
            int boxHeight = textRenderer.fontHeight;

            var label = getMessage();
            int labelWidth = textRenderer.getWidth(label);

            int textX = x() + (h() - boxWidth) / 2;
            int textY = y() + (h() - boxHeight) / 2;

            GUIUtil.drawText(context, textRenderer, Text.of(box), textX, textY, borderCol, false);
            GUIUtil.drawText(context, textRenderer, label, textX - labelWidth - 3, textY, PLCCols.TEXT.col, false);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder)
        {

        }
    }
}
