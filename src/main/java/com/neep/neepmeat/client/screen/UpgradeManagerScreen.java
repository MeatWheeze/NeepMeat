package com.neep.neepmeat.client.screen;

import com.neep.neepmeat.screen_handler.UpgradeManagerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class UpgradeManagerScreen extends HandledScreen<UpgradeManagerScreenHandler>
{
    public UpgradeManagerScreen(UpgradeManagerScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {

    }
}
