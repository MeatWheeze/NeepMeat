package com.neep.neepmeat.transport.client.screen.filter;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.StyledTooltipUser;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.item.filter.TagFilter;
import com.neep.neepmeat.transport.screen_handler.FilterScreenHandler;
import com.neep.neepmeat.util.TagSuggestions;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TagFilterWidget extends FilterEntryWidget<TagFilter>
{
    private final StyledTooltipUser parent;

    public TagFilterWidget(int w, int index, TagFilter filter, StyledTooltipUser parent, FilterScreenHandler handler)
    {
        super(w, 32, index, filter, handler);
        this.parent = parent;
    }

    @Override
    public void init()
    {
        super.init();

        addDrawableChild(new TagTextField(MinecraftClient.getInstance().textRenderer,
                x() + 2, y() + textRenderer.fontHeight + 2,
                w - 4, 16,
                this::update,
                filter.getTag() != null ? filter.getTag().id().toString() : ""));
    }

    private void update(String text)
    {
        Identifier id = Identifier.tryParse(text);
        if (TagSuggestions.INSTANCE.isValid(id))
        {
            filter.setTag(TagKey.of(Registries.ITEM.getKey(), id));
            handler.updateToServer.emitter().apply(index, filter.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        GUIUtil.drawText(context, textRenderer, "Tag filter", x() + 2, y() + 2, PLCCols.TEXT.col, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private class TagTextField extends NMTextField
    {
        private final Consumer<String> update;
        private List<Identifier> suggestions = List.of();

        public TagTextField(TextRenderer textRenderer, int x, int y, int width, int height, Consumer<String> update, String initial)
        {
            super(textRenderer, x, y, width, height, Text.empty());
            this.update = update;
            setText(initial);
            setChangedListener(this::onChanged);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (keyCode == GLFW.GLFW_KEY_TAB)
            {
                cycleSuggestion(Screen.hasShiftDown() ? -1 : 1);
            }
            else if (keyCode == GLFW.GLFW_KEY_ENTER)
            {
                confirmSuggestion();
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
        {
            int borderCol = isSelected() ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
            GUIUtil.renderBorderInner(context, x(), y(), w(), h(), borderCol, 0);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
        {
            super.renderButton(context, mouseX, mouseY, delta);

            int maxSuggestions = 5;
            if (!suggestions.isEmpty() && isActive())
            {
                int suggestionY = y2();

                int end = Math.min(suggestions.size(), maxSuggestions);
                int stride = textRenderer.fontHeight + 2;
//                int yOffsetEnd = end * stride;
//                context.fill(x(), y2(), x() + w(), y2() + yOffsetEnd, 0xBB331111);

                List<Text> texts = suggestions.stream().limit(maxSuggestions).map(i -> Text.of(i.toString())).toList();
                parent.renderTooltipText(context, texts, false, x(), suggestionY, PLCCols.TEXT.col);
            }
        }

        @Override
        protected int renderUnselectedText(DrawContext context, String string, boolean selectionWithin, int textStart, int m, int col, int j)
        {
            if (!suggestions.isEmpty())
            {
                GUIUtil.drawText(context, this.textRenderer, suggestions.get(0).toString(), textStart, m, PLCCols.INVALID.col, true);
            }

            return super.renderUnselectedText(context, string, selectionWithin, textStart, m, col, j);
        }

        @Override
        public String getPrefix()
        {
            return "#";
        }

        private void onChanged(String current)
        {
            suggestions = TagSuggestions.INSTANCE.get(current);
            update.accept(current);
        }

        private void cycleSuggestion(int distance)
        {
            Collections.rotate(suggestions, distance);
        }

        private void confirmSuggestion()
        {
            if (!suggestions.isEmpty())
                setText(suggestions.get(0).toString());
        }

        @Override
        protected int prefixCol()
        {
            return PLCCols.LINE_NUMBER.col;
        }

        @Override
        public void setFocused(boolean focused)
        {
            super.setFocused(focused);
        }
    }
}
