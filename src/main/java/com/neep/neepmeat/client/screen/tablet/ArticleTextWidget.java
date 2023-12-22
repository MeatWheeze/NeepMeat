package com.neep.neepmeat.client.screen.tablet;

import com.neep.neepmeat.guide.article.Article;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

import java.util.function.BiFunction;

public class ArticleTextWidget implements Element, Drawable, Selectable
{
    private int x, y, width, height;
    private final TextRenderer textRenderer;
    private final Article article;
    private BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(string, Style.EMPTY);

    public ArticleTextWidget(TextRenderer textRenderer, Article article)
    {
        this.textRenderer = textRenderer;
        this.article = article;
    }

    public void setDimensions(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public TextRenderer getTextRenderer()
    {
        return textRenderer;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
//        textRenderer.draw(text, x, y,
//         this.textRenderer.drawWithShadow(matrices, this.renderTextProvider.apply(string2, this.firstCharacterIndex), (float)n, (float)m, i);
//        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
//        for (int m = 0; m < l; ++m) {
//            OrderedText orderedText = this.cachedPage.get(m);
//        int m = 0;
//        this.textRenderer.draw(matrices, article, (float) (x + 2), (float) (y + m * this.textRenderer.fontHeight), 0x00FF00);
//        }

        int head = 0;
        for (Article.Content content : article.getContents())
        {
            head += content.render(matrices, x + 2, y + 2 + head, width, this);
        }

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
