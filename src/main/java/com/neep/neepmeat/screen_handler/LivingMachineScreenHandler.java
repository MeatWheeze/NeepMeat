package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerInventory;

public class LivingMachineScreenHandler extends BasicScreenHandler
{
    public LivingMachineScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(playerInventory, syncId);
    }

    public LivingMachineScreenHandler(PlayerInventory playerInventory, int syncId)
    {
        super(ScreenHandlerInit.LIVING_MACHINE, playerInventory, null, syncId, null);
    }
}
