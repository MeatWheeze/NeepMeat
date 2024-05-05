package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.screen_handler.LivingMachineScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class LivingMachineScreen extends HandledScreen<LivingMachineScreenHandler>
{
    public LivingMachineScreen(LivingMachineScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(context);


    }
}
