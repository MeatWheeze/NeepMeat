package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.button.NMButtonWidget;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.mixin.TextFieldWidgetAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class NMTextField extends TextFieldWidget implements ClickableWidget
{
    protected final TextRenderer textRenderer;
    private final BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(
            string, Style.EMPTY
    );
    protected boolean drawFancyBackground = true;

    protected TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) this;

    public NMTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
    {
        super(textRenderer, x, y, width, height, text);
        this.textRenderer = textRenderer;
        setDrawsBackground(false);
    }

    public NMTextField drawFancyBackground(boolean draw)
    {
        this.drawFancyBackground = draw;
        return this;
    }

    protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if (drawFancyBackground)
        {
            GUIUtil.drawNineSlicedTexture(context, NMButtonWidget.NM_WIDGETS_TEXTURE, getX(), getY(), this.getWidth(), this.getHeight(), 4, 200, 20, 0, 90);
        }
    }

    protected int getTextY()
    {
        return y() + (this.height - 8) / 2;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
    {
        renderBackground(context, mouseX, mouseY, delta);

        TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) this;

        int col = accessor.getEditable() ? PLCCols.TEXT.col : PLCCols.INVALID.col;
        int j = accessor.getSelectionStart() - accessor.getFirstCharacterIndex();
        int k = accessor.getSelectionEnd() - accessor.getFirstCharacterIndex();
        String string = this.textRenderer.trimToWidth(accessor.getText().substring(accessor.getFirstCharacterIndex()), this.getInnerWidth());
        boolean selectionWithin = j >= 0 && j <= string.length();
        boolean bl2 = this.isFocused() && accessor.getFocusedTicks() / 6 % 2 == 0 && selectionWithin;

        String prefix = getPrefix();
        int prefixStart = this.getX() + 4;
        int textStart = prefixStart + Math.max(textRenderer.getWidth(prefix), getTextStart());
        int m = getTextY();
        int n = textStart;

        if (k > string.length())
        {
            k = string.length();
        }

        if (!prefix.isEmpty())
        {
            GUIUtil.drawText(context, this.textRenderer, prefix, prefixStart, m, prefixCol(), false);
        }

        if (!string.isEmpty())
        {
            n = renderUnselectedText(context, string, selectionWithin, textStart, m, col, j);
        }

        boolean bl3 = accessor.getSelectionStart() < accessor.getText().length() || accessor.getText().length() >= accessor.callGetMaxLength();
        int o = n;
        if (!selectionWithin)
        {
            o = j > 0 ? textStart + this.width : textStart;
        }
        else if (bl3)
        {
            o = n - 1;
            --n;
        }

        if (!string.isEmpty() && selectionWithin && j < string.length())
        {
            GUIUtil.drawText(context, this.textRenderer, this.renderTextProvider.apply(string.substring(j), accessor.getSelectionStart()), n, m, col, true);
        }

        if (bl2)
        {
            if (bl3)
            {
                context.fill(RenderLayer.getGuiOverlay(), o, m - 1, o + 1, m + 1 + 9, -3092272);
            }
            else
            {
                GUIUtil.drawText(context, this.textRenderer, "_", o, m, col, true);
            }
        }

        if (k != j)
        {
            int p = textStart + this.textRenderer.getWidth(string.substring(0, k));
            accessor.callDrawSelectionHighlight(context, o, m - 1, p - 1, m + 1 + 9);
        }
    }

    protected int renderUnselectedText(DrawContext context, String string, boolean selectionWithin, int textStart, int m, int col, int j)
    {
        String string2 = selectionWithin ? string.substring(0, j) : string;
        return GUIUtil.drawText(context, this.textRenderer, this.renderTextProvider.apply(string2, accessor.getFirstCharacterIndex()), textStart, m, col, true);
    }

    protected String getPrefix()
    {
        return "";
    }

    protected int getTextStart()
    {
        return 0;
    }

    protected int prefixCol()
    {
        return PLCCols.INVALID.col;
    }

    @Override
    public void setChangedListener(Consumer<String> changedListener)
    {
        super.setChangedListener(changedListener);
    }

    @Override
    public int x()
    {
        return getX();
    }

    @Override
    public int y()
    {
        return getY();
    }

    @Override
    public int w()
    {
        return width;
    }

    @Override
    public int h()
    {
        return height;
    }

    @Override
    public void setPos(int x, int y)
    {
        setX(x);
        setY(y);
    }
}
