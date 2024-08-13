package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.neepbus.screen.NeepBusRangeConfigScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class NeepBusRangeConfigScreen extends NeepBusConfigScreen<NeepBusRangeConfigScreenHandler>
{
    private final RangeConfigWidget rangeConfig;

    public NeepBusRangeConfigScreen(NeepBusRangeConfigScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);

        rangeConfig = new RangeConfigWidget(6, 0, 100, 100, handler.rangeConfig);
    }

    @Override
    protected void init()
    {
        super.init();

        addDrawableChild(rangeConfig);

        // Attach the range config to the bottom of the port config
        rangeConfig.setW(backgroundWidth);
        rangeConfig.init();

        rangeConfig.setPos(x, y + backgroundHeight + 10);
        backgroundHeight += rangeConfig.h();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    protected void handledScreenTick()
    {
        super.handledScreenTick();
        rangeConfig.tick();
    }
}
