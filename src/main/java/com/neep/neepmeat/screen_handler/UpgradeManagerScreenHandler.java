package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class UpgradeManagerScreenHandler extends ScreenHandler
{
    public UpgradeManagerScreenHandler(int syncId, PlayerInventory pi)
    {
        super(ScreenHandlerInit.UPGRADE_MANAGER, syncId);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}