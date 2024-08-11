package com.neep.meatweapons.client.screen.meatgun;

import com.neep.meatlib.client.ClientChannelSender;
import com.neep.meatlib.network.Sender;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.screen.TinkerTableScreenHandler;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.client.screen.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TinkerTableScreen extends BaseHandledScreen<TinkerTableScreenHandler>
{
    public static final Identifier WIDGETS_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/gui/tinker_table/widgets.png");
    private final DisplayPane displayPane;
    private final TreePane treePane;
    private final Sender<TinkerTableScreenHandler.SlotClick> sender;

    public TinkerTableScreen(TinkerTableScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        treePane = new TreePane(handler, handler.getSlot(0));
        displayPane = new DisplayPane(handler.getSlot(0));

        this.sender = new ClientChannelSender<>(TinkerTableScreenHandler.CHANNEL_ID, TinkerTableScreenHandler.CHANNEL_FORMAT);
    }

    @Override
    protected void init()
    {
        backgroundWidth = TinkerTableScreenHandler.BACKGROUND_WIDTH;
        backgroundHeight = TinkerTableScreenHandler.BACKGROUND_HEIGHT;
        super.init();

        OldBackground border = new OldBackground(x, y, backgroundWidth, backgroundHeight, 6, () -> PLCCols.BORDER.col);
        Rectangle withoutPadding = border.withoutPadding();
        Rectangle bounds = new Rectangle.Immutable(withoutPadding.x(), withoutPadding.y(), withoutPadding.w(), withoutPadding.h() - 19);
        addDrawable(border);

        // Slot
        addDrawable(new ItemBorderSlot(handler.getSlot(0), bounds.x(), withoutPadding.y() + withoutPadding.h() - 19, () -> PLCCols.BORDER.col));

        // Hotbar
        addDrawable(new Border(bounds.x() + 20 + 1, withoutPadding.y() + withoutPadding.h() - 19, 18 * 9 + 2, 20, 0, () -> PLCCols.BORDER.col));

        // Inventory
        var inventoryBorder = new Border(bounds.x() + 20 + 1, withoutPadding.y() + withoutPadding.h() - 80, 18 * 9 + 2, 3 * 19 + 3, 0, () -> PLCCols.BORDER.col);
        addDrawable(inventoryBorder);

        Rectangle.Mutable displayPaneBounds = new Rectangle.Mutable(bounds)
                .setH(inventoryBorder.y() - bounds.y() - 1)
                .setW(18 * 9 + 20 + 3);
        displayPane.init(displayPaneBounds);
        addDrawableChild(displayPane);

        Rectangle.Mutable treePaneBounds = new Rectangle.Mutable(withoutPadding)
                .setX(displayPaneBounds.x() + displayPaneBounds.w() + 1)
                .setW(bounds.x() + bounds.w() - (displayPaneBounds.x() + displayPaneBounds.w()));
        treePane.init(treePaneBounds);
        addDrawableChild(treePane);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (treePane.isMouseOver(mouseX, mouseY))
        {
            return treePane.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        else if (displayPane.isMouseOver(mouseX, mouseY))
        {
            return displayPane.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected void handledScreenTick()
    {
        treePane.tick();
        displayPane.tick();
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

    private class ItemBorderSlot extends BorderSlot
    {
        private final Slot slot;
        private final Text tooltip = Text.translatable("text.meatweapons.tinker_table.item_here");

        public ItemBorderSlot(Slot slot, int x, int y, Supplier<Integer> col)
        {
            super(x, y, col);
            this.slot = slot;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta)
        {
            super.render(context, mouseX, mouseY, delta);

            if (!slot.hasStack())
            {
                GUIUtil.drawTexture(WIDGETS_TEXTURE, context, x + 2, y + 2, 19, 1, 16, 16);
                if (isWithin(mouseX, mouseY))
                {
                    renderTooltipText(context, List.of(tooltip), false, mouseX, mouseY, PLCCols.BORDER.col);
//                    context.drawTooltip(textRenderer, List.of(tooltip), mouseX, mouseY);
                }
            }
        }
    }
}
