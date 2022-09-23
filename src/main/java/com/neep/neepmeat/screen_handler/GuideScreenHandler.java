package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public class GuideScreenHandler extends ScreenHandler
{
    public GuideScreenHandler(int syncId, PlayerInventory inventory)
    {
        super(ScreenHandlerInit.GUIDE, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}
