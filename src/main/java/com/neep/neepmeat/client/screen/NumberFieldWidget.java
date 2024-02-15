package com.neep.neepmeat.client.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NumberFieldWidget extends TextFieldWidget
{
    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
    {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public void write(String text)
    {
        if (!text.matches("[0-9]*")) return;
        super.write(text);
    }
}
