package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.neepbus.screen.NeepBusScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class NeepBusConfigScreen extends HandledScreen<NeepBusScreenHandler>
{
    public NeepBusConfigScreen(NeepBusScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void init()
    {
        super.init();

//        backgroundWidth =
    }
}
