package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.StyledTooltipUser;
import com.neep.neepmeat.client.screen.util.CheckboxWidget;
import com.neep.neepmeat.client.screen.util.AbstractClickableWidget;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.client.screen.util.Point;
import com.neep.neepmeat.item.filter.FilterList;
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

    public ItemFilterWidget(int w, int index, FilterList.Entry entry, ItemFilter filter, StyledTooltipUser parent, FilterScreenHandler handler)
    {
        super(w, 50, index, NeepMeat.translationKey("screen", "filter.item_filter"), entry, filter, handler);
        this.parent = parent;
    }

    @Override
    public void init()
    {
        super.init();

        int slotsHeight = 2 * 18 - 1;

        int slotsY = y + textRenderer.fontHeight + 4;
        int slotsX = x + 2;

        int slotsW = 5;
        int slotsH = 2;
        for (int j = 0; j < slotsH; j++)
        {
            for (int i = 0; i < slotsW; i++)
            {
                addDrawableChild(new ItemSlotWidget(slotsX + i * 17, slotsY + 17 * j, i + j * slotsW));
            }
        }

        addDrawableChild(new CheckboxWidget(x + 17 * 5 + 4, y + textRenderer.fontHeight + 4, 50, 16, () -> filter.ignoreDamage(), Text.of("Use damage"), (b, t) ->
        {
            filter.setUseDamage(t);
            updateToServer();
        }));

        addDrawableChild(new CheckboxWidget(x + 17 * 5 + 4, y + textRenderer.fontHeight + 4 + 17, 50, 16, () -> filter.ignoreNbt(), Text.of("Use all NBT"), (b, t) ->
        {
            filter.setUseNbt(t);
            updateToServer();
        }));
    }

    class ItemSlotWidget extends AbstractClickableWidget implements Point.Mutable
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
            context.drawItemInSlot(textRenderer, stack, this.getX() + 1, this.getY() + 1);

            if (isMouseOver(mouseX, mouseY) && !stack.isEmpty() && handler.getCursorStack().isEmpty())
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
