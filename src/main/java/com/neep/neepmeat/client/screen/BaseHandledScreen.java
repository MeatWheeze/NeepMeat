package com.neep.neepmeat.client.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public abstract class BaseHandledScreen<T extends ScreenHandler> extends HandledScreen<T> implements StyledTooltipUser
{
    public BaseHandledScreen(T handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }

    @Override
    public TextRenderer textRenderer()
    {
        return textRenderer;
    }
}
