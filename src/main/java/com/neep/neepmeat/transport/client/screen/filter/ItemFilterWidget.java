package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.item.filter.ItemFilter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ItemFilterWidget extends FilterEntryWidget
{
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final ItemFilter filter;

    public ItemFilterWidget(int w, int index, ItemFilter filter)
    {
        super(w, index);
        this.filter = filter;
    }

    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        GUIUtil.drawText(context, textRenderer, Text.of("Item filter"), x + 3, y + 3, PLCCols.TEXT.col, true);
    }
}
