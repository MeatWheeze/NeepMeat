package com.neep.neepmeat.guide.article;

import com.neep.neepmeat.client.screen.plc.MonoTextRenderer;
import com.neep.neepmeat.client.screen.tablet.ArticleTextWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class CodeContent implements Article.Content
{
    private final Text text;
    private static final MonoTextRenderer RENDERER = new MonoTextRenderer();

    public CodeContent(MutableText text)
    {
        this.text = text;
    }

    @Override
    public int render(MatrixStack matrices, float x, float y, float width, double scroll, ArticleTextWidget parent)
    {
        List<OrderedText> lines = RENDERER.wrapLines(text, (int) width);
        int fontHeight = RENDERER.fontHeight();
        int i = 0;
        while (i < lines.size())
        {
            float head = y + i * fontHeight;
            if (head > parent.getTop() && (head + fontHeight) < parent.getBottom())
            {
                RENDERER.draw(matrices, lines.get(i), x + 5, (float) (y - scroll + i * fontHeight), 0xFF00BE50);
            }
            ++i;
        }
        return i * fontHeight;
    }
}
