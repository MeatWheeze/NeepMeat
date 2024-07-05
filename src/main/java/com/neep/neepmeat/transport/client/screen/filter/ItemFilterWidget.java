package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.StyledTooltipUser;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Point;
import com.neep.neepmeat.item.filter.ItemFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemFilterWidget extends FilterEntryWidget<ItemFilter>
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;
    private final ItemRenderer itemRenderer = client.getItemRenderer();
    private final StyledTooltipUser parent;

    public ItemFilterWidget(int w, int index, ItemFilter filter, StyledTooltipUser parent, FilterScreenHandler handler)
    {
        super(w, 51, index, filter, handler);
        this.parent = parent;
    }

    @Override
    public void init()
    {
        super.init();

        int slotsHeight = 2 * 18 - 1;

        int slotsY = y + textRenderer.fontHeight + 4;
        int slotsX = x + 2;

        for (int j = 0; j < 2; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                addWWidget(new ItemSlotWidget(slotsX + i * 17, slotsY + 17 * j, i + j * 3));
            }
        }
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        GUIUtil.drawText(context, textRenderer, Text.of("Item filter"), x + 3, y + 3, PLCCols.TEXT.col, true);
    }


    class ItemSlotWidget extends ClickableWidget implements Point.Mutable
    {
        private final int slotIndex;

        public ItemSlotWidget(int x, int y, int slotIndex)
        {
            super(x, y, 18, 18, Text.empty());
            this.slotIndex = slotIndex;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            GUIUtil.renderBorderInner(context, this.getX(), this.getY(), 18, 18, PLCCols.BORDER.col, 0);
            GUIUtil.renderBorderInner(context, this.getX(), this.getY(), 18, 18, PLCCols.TRANSPARENT.col, -1);

            ItemStack stack = filter.getItem(slotIndex).toStack(1);
            context.drawItem(stack, this.getX() + 1, this.getY() + 1);

            if (isMouseOver(mouseX, mouseY) && !stack.isEmpty())
            {
                parent.renderTooltipText(context, stack.getTooltip(client.player, TooltipContext.BASIC), false, mouseX, mouseY, PLCCols.TEXT.col);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);

            filter.setItem(slotIndex, ItemVariant.of(handler.getCursorStack()));
            updateToServer();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder)
        {

        }
    }
}
