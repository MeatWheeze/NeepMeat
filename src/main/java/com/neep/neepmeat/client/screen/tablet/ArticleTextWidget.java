package com.neep.neepmeat.client.screen.tablet;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public class ArticleTextWidget implements Element, Drawable, Selectable
{
    private int x, y, width, height;
    private final TextRenderer textRenderer;
    private Text text;
    private BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(string, Style.EMPTY);

    public ArticleTextWidget(TextRenderer textRenderer, Text text)
    {
        this.textRenderer = textRenderer;
        this.text = text;
    }

    public void setDimensions(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setText(Text text)
    {
        this.text = text;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
//        textRenderer.draw(text, x, y,
//         this.textRenderer.drawWithShadow(matrices, this.renderTextProvider.apply(string2, this.firstCharacterIndex), (float)n, (float)m, i);
//        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
//        for (int m = 0; m < l; ++m) {
//            OrderedText orderedText = this.cachedPage.get(m);
        int m = 0;
        this.textRenderer.draw(matrices, text, (float) (x), (float) (y + m * this.textRenderer.fontHeight), 0);
//        }
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
