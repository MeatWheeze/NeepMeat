package com.neep.neepmeat.client.screen.button;

import com.neep.neepmeat.client.screen.button.NMButtonWidget;
import net.minecraft.text.Text;

public class NMToggleButtonWidget extends NMButtonWidget
{
    public NMToggleButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier)
    {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }
}
