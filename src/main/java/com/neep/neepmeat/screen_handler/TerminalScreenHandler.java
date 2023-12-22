package com.neep.neepmeat.screen_handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class TerminalScreenHandler extends ScreenHandler
{
    public TerminalScreenHandler()
    {
        super(null, 0);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return false;
    }
}
