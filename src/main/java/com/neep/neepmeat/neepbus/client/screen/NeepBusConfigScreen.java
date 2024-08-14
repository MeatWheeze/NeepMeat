package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.neepbus.screen.NeepBusConfigScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class NeepBusConfigScreen<T extends NeepBusConfigScreenHandler> extends HandledScreen<T>
{
    private final AddressConfigWidget configWidget;

    public NeepBusConfigScreen(T handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        configWidget = new AddressConfigWidget(0, 0, 100, 100, handler.configHandler);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void init()
    {
        addDrawableChild(configWidget);
        configWidget.init();

        backgroundWidth = configWidget.w();
        backgroundHeight = configWidget.h();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        configWidget.setPos(x, y);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }
}
