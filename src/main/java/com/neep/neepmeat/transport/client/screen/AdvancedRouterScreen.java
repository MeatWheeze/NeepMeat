package com.neep.neepmeat.transport.client.screen;

import com.neep.neepmeat.client.screen.BaseHandledScreen;
import com.neep.neepmeat.screen_handler.AdvancedRouterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class AdvancedRouterScreen extends BaseHandledScreen<AdvancedRouterScreenHandler>
{
    public AdvancedRouterScreen(AdvancedRouterScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }
}
