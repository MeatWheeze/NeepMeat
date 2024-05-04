package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.button.NMButtonWidget;
import com.neep.neepmeat.mixin.TextFieldWidgetAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public class NMTextField extends TextFieldWidget
{
    private final TextRenderer textRenderer;
    protected boolean drawFancyBackground = true;

    private final BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(
            string, Style.EMPTY
    );

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

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if (drawFancyBackground)
        {
            context.drawNineSlicedTexture(NMButtonWidget.NM_WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, 90);
        }

        TextFieldWidgetAccessor accessor = (TextFieldWidgetAccessor) this;

        int i = accessor.getEditable() ? PLCCols.TEXT.col : PLCCols.INVALID.col;
        int j = accessor.getSelectionStart() - accessor.getFirstCharacterIndex();
        int k = accessor.getSelectionEnd() - accessor.getFirstCharacterIndex();
        String string = this.textRenderer.trimToWidth(accessor.getText().substring(accessor.getFirstCharacterIndex()), this.getInnerWidth());
        boolean bl = j >= 0 && j <= string.length();
        boolean bl2 = this.isFocused() && accessor.getFocusedTicks() / 6 % 2 == 0 && bl;
        int l = this.getX() + 4;
        int m = this.getY() + (this.height - 8) / 2;
        int n = l;
        if (k > string.length())
        {
            k = string.length();
        }

        if (!string.isEmpty())
        {
            String string2 = bl ? string.substring(0, j) : string;
            n = context.drawTextWithShadow(this.textRenderer, this.renderTextProvider.apply(string2, accessor.getFirstCharacterIndex()), l, m, i);
        }

        boolean bl3 = accessor.getSelectionStart() < accessor.getText().length() || accessor.getText().length() >= accessor.callGetMaxLength();
        int o = n;
        if (!bl)
        {
            o = j > 0 ? l + this.width : l;
        }
        else if (bl3)
        {
            o = n - 1;
            --n;
        }

        if (!string.isEmpty() && bl && j < string.length())
        {
            context.drawTextWithShadow(this.textRenderer, this.renderTextProvider.apply(string.substring(j), accessor.getSelectionStart()), n, m, i);
        }

        if (bl2)
        {
            if (bl3)
            {
                context.fill(RenderLayer.getGuiOverlay(), o, m - 1, o + 1, m + 1 + 9, -3092272);
            }
            else
            {
                context.drawTextWithShadow(this.textRenderer, "_", o, m, i);
            }
        }

        if (k != j)
        {
            int p = l + this.textRenderer.getWidth(string.substring(0, k));
            accessor.callDrawSelectionHighlight(context, o, m - 1, p - 1, m + 1 + 9);
        }
    }
}
