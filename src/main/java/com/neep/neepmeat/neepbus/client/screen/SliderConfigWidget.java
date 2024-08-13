package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.neepbus.screen.SliderConfigHandler;
import net.minecraft.text.Text;

public class SliderConfigWidget extends RangeConfigWidget
{
    public SliderConfigWidget(int x, int y, int w, int h, SliderConfigHandler handler)
    {
        super(x, y, w, h, handler);

        addTextField(new TextField(textRenderer, x, y, w, 8, Text.of("Interval: ")));
    }
}
