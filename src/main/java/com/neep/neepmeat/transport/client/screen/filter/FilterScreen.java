package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.plc.PLCScreenButton;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.BorderScrollRight;
import com.neep.neepmeat.client.screen.util.PlayerSlotsBorder;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.item.filter.*;
import com.neep.neepmeat.mixin.AbstractParentElementAccessor;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FilterScreen extends BaseHandledScreen<FilterScreenHandler>
{
    private final List<FilterEntryWidget<?>> entries = new ArrayList<>();

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
        this.backgroundWidth = FilterScreenHandler.BACKGROUND_WIDTH;
        this.backgroundHeight = FilterScreenHandler.BACKGROUND_HEIGHT;
        super.init();

//        Background background = new Background(x, y, backgroundWidth, backgroundHeight, 6, () -> PLCCols.BORDER.col);
        Border background = addDrawable(new Border(x, y, backgroundWidth, backgroundHeight, 6, () -> PLCCols.BORDER.col));
        Rectangle withoutPadding = background.withoutPadding();

        int entriesX = withoutPadding.x();
        int entriesWidth = withoutPadding.w();
        int entriesHeight = 150;

        addDrawable(background);

        int buttonX = withoutPadding.x() + entriesWidth + 1 - 17;
        int buttonY = withoutPadding.y() + entriesHeight + 1;
        addDrawableChild(new AddFilterButton(buttonX, buttonY, 128, Filters.ITEM, Text.empty()));
        addDrawableChild(new AddFilterButton(buttonX, buttonY + 17, 144, Filters.TAG, Text.empty()));

        Rectangle entriesBounds = new Rectangle.Immutable(withoutPadding.x(), withoutPadding.y(), entriesWidth, entriesHeight);
        entriesBorder = new BorderScrollRight(entriesBounds, 0, () -> PLCCols.BORDER.col);

//        Rectangle inventoryBounds = new Rectangle.Immutable(
//        int inventoryX =
//        var inventoryBorder = new Border(entriesX, withoutPadding.y() + withoutPadding.h() - 76, 18 * 9 + 2, 3 * 19, 0, () -> PLCCols.BORDER.col);
//        var hotbarBorder = new Border(entriesX, withoutPadding.y() + withoutPadding.h() - 19, 18 * 9 + 2, 20, 0, () -> PLCCols.BORDER.col);
        var inventoryBorder = new PlayerSlotsBorder(entriesX, entriesBounds.y() + entriesBounds.h() + 1, () -> PLCCols.BORDER.col);
        addDrawable(inventoryBorder);
//        addDrawable(hotbarBorder);

        createEntries();
    }

    private void createEntries()
    {
        FilterList filters = handler.getFilters();
        int yOff = (int) (entriesBorder.y() + scroll) + 2;
        int xOff = entriesBorder.x() + 2;

        // In order make the state of each EntryWidget persistent across syncs, some jank is necessary.
        for (int i = 0; i < filters.size(); i++)
        {
            Filter filter = filters.getFilter(i);
            FilterEntryWidget<?> widget;
            if (i < entries.size() && entries.get(i).filter.getType() != filter.getType())
            {
                // Change type
                widget = createWidget(i, filter);
                entries.set(i, widget);
                widget.init();
            }
            else if (i == entries.size())
            {
                widget = createWidget(i, filter);
                entries.add(i, widget);
                widget.init();
            }
            else
            {
                widget = entries.get(i);
                widget.updateFilter(filter);
            }

            widget.setPos(xOff, yOff);
            yOff += widget.h() - 1;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        entriesBorder.render(context, mouseX, mouseY, delta, scroll / maxScroll());

        context.enableScissor(entriesBorder.x(), entriesBorder.y() + 2, entriesBorder.x() + entriesBorder.w(), entriesBorder.y() + entriesBorder.h() - 2);
        for (var entry : entries)
        {
            entry.render(context, mouseX, mouseY, delta);
        }
        context.disableScissor();

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private float maxScroll()
    {
        return -100;
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
            if (entry.mouseClicked(mouseX, mouseY, button))
            {
                setFocused(entry);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc())
        {
            this.close();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DELETE)
        {
            for (var entry : entries)
            {
                if (entry.isFocused())
                {
                    handler.removeFilter.emitter().apply(entry.index);
                }
            }
        }

        for (var entry : entries)
        {
            if (entry.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        for (var entry : entries)
        {
            if (entry.charTyped(chr, modifiers))
                return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        scroll = (float) MathHelper.clamp(scroll + amount * 2, maxScroll(), 0);

        int yOff = (int) (entriesBorder.y() + scroll) + 2;
        int xOff = entriesBorder.x() + 2;
        for (var entry : entries)
        {
            entry.setPos(xOff, yOff);
            yOff += entry.h() + 1;
        }

        return true;
    }

    public void updateToClient(FilterList filter)
    {
        handler.updateToClient(filter);
        createEntries();
    }

    @Override
    public void setFocused(@Nullable Element focused)
    {
        AbstractParentElementAccessor accessor = (AbstractParentElementAccessor) this;
        if (accessor.getFieldFocused() != null && accessor.getFieldFocused() != focused)
            accessor.getFieldFocused().setFocused(false);

        if (focused != null)
        {
            focused.setFocused(true);
        }

        accessor.setFieldFocused(focused);
    }

    // Jank!
    private FilterEntryWidget<?> createWidget(int index, Filter filter)
    {
        int w = entriesBorder.w() - 4;
        if (filter instanceof ItemFilter itemFilter)
        {
            return new ItemFilterWidget(w, index, itemFilter, this, handler);
        }
        else if (filter instanceof TagFilter tagFilter)
        {
            return new TagFilterWidget(w, index, tagFilter, this, handler);
        }
        return new EmptyFilterWidget(w, index);
    }

    private class AddFilterButton extends PLCScreenButton
    {
        private final int u;
        private final Filter.Constructor<?> filter;
        private final Text tooltip;

        public AddFilterButton(int x, int y, int u, Filter.Constructor<?> filter, Text tooltip)
        {
            super(x, y, Text.empty());
            this.u = u;
            this.filter = filter;
            this.tooltip = tooltip;
        }

        @Override
        public void renderTooltip(DrawContext matrices, int mouseX, int mouseY)
        {

        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
            handler.addFilter(filter);


        }

        @Override
        protected int getU()
        {
            return u;
        }
    }

    public class EmptyFilterWidget extends FilterEntryWidget<Filter>
    {
        public EmptyFilterWidget(int w, int index)
        {
            super(w, 10, index, null, FilterScreen.this.handler);
        }
    }

    ;
}
