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
    private Text text;

    public TextContent(MutableText text)
    {
        this.text = text;
    }

    @Override
    public int render(MatrixStack matrices, float x, float y, float width, ArticleTextWidget parent)
    {
        TextRenderer renderer = parent.getTextRenderer();
        List<OrderedText> lines = renderer.wrapLines(text, (int) width);
        int i = 0;
        for (;i < lines.size(); ++i)
        {
            parent.getTextRenderer().draw(matrices, lines.get(i), x, y + i * renderer.fontHeight, 0x00FF00);
        }
        return i * renderer.fontHeight;
    }
}
