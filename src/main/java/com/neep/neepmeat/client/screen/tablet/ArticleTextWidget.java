package com.neep.neepmeat.client.screen.tablet;

import com.neep.neepmeat.guide.article.Article;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ArticleTextWidget implements Element, Drawable, Selectable
{
//    private int lineStart
    private double scrollAmount;
    private double scrollLag;
    private int x, y, width, height;
    private final TextRenderer textRenderer;
    private final Article article;

    public ArticleTextWidget(TextRenderer textRenderer, Article article)
    {
        this.textRenderer = textRenderer;
        this.article = article != null ? article : Article.EMPTY;
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

    public int getTop()
    {
        return (int) (y + scrollAmount);
    }

    public int getBottom()
    {
        return (int) (y + height + scrollAmount);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        scrollAmount = MathHelper.lerp(0.4, scrollAmount, scrollLag);
        int head = 0;
        for (Article.Content content : article.getContents())
        {
            head += content.render(matrices, x + 2, y + 2 + head, width, scrollAmount, this);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        scrollLag = MathHelper.clamp(scrollLag - 7 * amount, 0, Double.MAX_VALUE);
        return true;
    }
}
