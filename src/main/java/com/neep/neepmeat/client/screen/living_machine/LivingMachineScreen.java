package com.neep.neepmeat.client.screen.living_machine;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.screen_handler.LivingMachineScreenHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class LivingMachineScreen extends HandledScreen<LivingMachineScreenHandler>
{
    private final MetricsPane metricsPane;

    public LivingMachineScreen(LivingMachineScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.metricsPane = new MetricsPane(handler);
    }

    @Override
    protected void init()
    {
        backgroundWidth = 400;
        backgroundHeight = 200;
        super.init();

        var mainBorder = addDrawable(new Border(x, y, backgroundWidth, backgroundHeight, 2, () -> PLCCols.BORDER.col));
        Rectangle withoutPadding = mainBorder.withoutPadding();
        float fraction = 0.4f;
        int metricsWidth = (int) (fraction * withoutPadding.w());
        int graphsWidth = (int) ((1 - fraction) * withoutPadding.w());
        metricsPane.init(new Rectangle.Mutable(withoutPadding).setW(metricsWidth));
        addDrawableChild(metricsPane);
        var graphs = addDrawable(new Border(new Rectangle.Immutable(metricsPane.border.x() + metricsPane.border.w() + 1, withoutPadding.y(), graphsWidth, withoutPadding.h()), 2, () -> PLCCols.BORDER.col));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {

    }

    @Override
    protected void handledScreenTick()
    {
        super.handledScreenTick();

    }

    static abstract class PaneWidget implements Drawable, Element, Selectable
    {
        protected boolean focused;
        protected Rectangle bounds;
        protected Border border;

        public void init(Rectangle parentSize)
        {
            this.bounds = new Rectangle.Immutable(parentSize);
            this.border = new Border(bounds, 0, () -> PLCCols.BORDER.col);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            border.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY)
        {
            return bounds.isWithin(mouseX, mouseY);
        }

        @Override
        public void setFocused(boolean focused)
        {
            this.focused = focused;
        }

        @Override
        public boolean isFocused()
        {
            return focused;
        }

        @Override
        public SelectionType getType()
        {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }
    }
}
