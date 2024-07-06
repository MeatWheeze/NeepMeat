package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.item.filter.TagFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import com.neep.neepmeat.util.TagSuggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TagFilterWidget extends FilterEntryWidget<TagFilter>
{
    public TagFilterWidget(int w, int index, TagFilter filter, FilterScreenHandler handler)
    {
        super(w, 41, index, filter, handler);
    }

    @Override
    public void init()
    {
        super.init();

        addDrawableChild(new TagTextField(MinecraftClient.getInstance().textRenderer, x() + 2, y() + 2, w - 4, 16, Text.empty()));
    }

    private static class TagTextField extends NMTextField
    {
        public TagTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
        {
            super(textRenderer, x, y, width, height, text);
            setChangedListener(this::suggestTag);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
        {
            int borderCol = isSelected() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
            GUIUtil.renderBorderInner(context, x(), y(), w(), h(), borderCol, 0);
        }

        @Override
        public String getPrefix()
        {
            return "#";
        }

        private void suggestTag(String current)
        {
            System.out.println(TagSuggestions.INSTANCE.get(current));
        }

        @Override
        protected int prefixCol()
        {
            return PLCCols.INVALID.col;
        }
    }
}
