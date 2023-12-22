package com.neep.neepmeat.guide.article;

import com.neep.neepmeat.client.screen.tablet.ArticleTextWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class TextContent implements Article.Content
{
    private final Text text;

    public TextContent(MutableText text)
    {
        this.text = text;
    }

    @Override
    public int render(MatrixStack matrices, float x, float y, float width, double scroll, ArticleTextWidget parent)
    {
        TextRenderer renderer = parent.getTextRenderer();
        List<OrderedText> lines = renderer.wrapLines(text, (int) width);
        int i = 0;
        while (i < lines.size())
        {
            float head = y + i * renderer.fontHeight;
            if (head > parent.getTop() && (head + renderer.fontHeight) < parent.getBottom())
            {
                parent.getTextRenderer().draw(matrices, lines.get(i), x, (float) (y - scroll + i * renderer.fontHeight), 0x00FF00);
            }
            ++i;
        }
        return i * renderer.fontHeight;
    }
}
