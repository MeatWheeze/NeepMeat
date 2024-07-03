package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.BorderScrollRight;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.item.filter.Filter;
import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.item.filter.ItemFilter;
import com.neep.neepmeat.item.filter.TagFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class FilterScreen extends BaseHandledScreen<FilterScreenHandler>
{
    private final List<FilterEntryWidget> entries = new ArrayList<>();

    private float scroll;
    private BorderScrollRight entriesBorder;

    public FilterScreen(FilterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);

        handler.channel.receiver(handler::receiveFilter);

    }

    @Override
    protected void init()
    {
        this.backgroundWidth = 300;
        this.backgroundHeight = 200;
        super.init();

        Background background = new Background(x, y, backgroundWidth, backgroundHeight, 6, () -> PLCCols.BORDER.col);
        Rectangle withoutPadding = background.withoutPadding();

        addDrawable(background);

        entriesBorder = new BorderScrollRight(withoutPadding, 0, () -> PLCCols.BORDER.col);

        createEntries();
    }

    private void createEntries()
    {
        entries.clear();

        FilterList filters = handler.getFilters();
        for (int i = 0; i < filters.size(); i++)
        {
            FilterEntryWidget widget = createWidget(i, filters.getEntries().get(i));
            entries.add(widget);
            widget.init();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        entriesBorder.render(context, mouseX, mouseY, delta, scroll / maxScroll());

        context.enableScissor(entriesBorder.x(), entriesBorder.y(), entriesBorder.x() + entriesBorder.w(), entriesBorder.y() + entriesBorder.h());
        for (var entry : entries)
        {
            entry.render(context, mouseX, mouseY, delta);
        }
        context.disableScissor();
    }

    private float maxScroll()
    {
        return -10;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (var entry : entries)
        {
            entry.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        scroll = (float) MathHelper.clamp(scroll + amount, maxScroll(), 0);

        int yOff = (int) (entriesBorder.y() + scroll);
        int xOff = entriesBorder.x();
        for (var entry : entries)
        {
            entry.setPos(xOff, yOff);
            yOff += entry.h();
        }

        return true;
    }

    @Override
    public void close()
    {
        super.close();
    }

    // Jank!
    private FilterEntryWidget createWidget(int index, Filter filter)
    {
        int w = entriesBorder.w();
        if (filter instanceof ItemFilter itemFilter)
        {
            return new ItemFilterWidget(w, index, itemFilter);
        }
        else if (filter instanceof TagFilter tagFilter)
        {
            return new TagFilterWidget(w, index, tagFilter);
        }
        return new EmptyFilterWidget(w, index);
    }

    public static abstract class FilterEntryWidget
    {
        protected final List<ClickableWidget> children = new ArrayList<>();

        private int x;
        private int y;

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
            GUIUtil.renderBorder(context, x + 2, y + 2, w - 5, 40, PLCCols.BORDER.col, 0);
        }

        public int h()
        {
            return h;
        }
    }

    public static class EmptyFilterWidget extends FilterEntryWidget
    {
        public EmptyFilterWidget(int w, int index)
        {
            super(w, index);
        }
    };
}
