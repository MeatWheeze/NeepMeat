package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.button.NMButtonWidget;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.BorderScrollRight;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.item.filter.*;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
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
        handler.updateToClient.receiver(this::updateToClient);
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

        addDrawableChild(new AddFilterButton(withoutPadding.x(), withoutPadding.y(), Filters.ITEM, Text.empty(), b -> {}));
        addDrawableChild(new AddFilterButton(withoutPadding.x(), withoutPadding.y() + 18, Filters.TAG, Text.empty(), b -> {}));

        Rectangle entriesBounds = new Rectangle.Immutable(withoutPadding.x() + 18, withoutPadding.y(), withoutPadding.w() - 18, withoutPadding.h());
        entriesBorder = new BorderScrollRight(entriesBounds, 0, () -> PLCCols.BORDER.col);

        createEntries();
    }

    private void createEntries()
    {
        entries.clear();

        FilterList filters = handler.getFilters();
        int yOff = (int) (entriesBorder.y() + scroll);
        int xOff = entriesBorder.x();
        for (int i = 0; i < filters.size(); i++)
        {
            FilterEntryWidget widget = createWidget(i, filters.getFilter(i));
            entries.add(widget);
            widget.init();

            widget.setPos(xOff,yOff);
            yOff += widget.h();
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

    public void updateToClient(FilterList filter)
    {
        handler.updateToClient(filter);
        createEntries();
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

    private class AddFilterButton extends NMButtonWidget
    {
        private final Filter.Constructor<?> filter;
        private final Text tooltip;

        public AddFilterButton(int x, int y, Filter.Constructor<?> filter, Text tooltip, PressAction onPress)
        {
            super(x, y, 16, 16, Text.empty(), onPress);
            this.filter = filter;
            this.tooltip = tooltip;
            showBackground(false);
        }

        @Override
        public void onPress()
        {
            super.onPress();
            handler.addFilter(filter);
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
