package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class GuideScreenHandler extends ScreenHandler
{
    public GuideScreenHandler(int syncId, PlayerInventory inventory)
    {
        super(ScreenHandlerInit.GUIDE, syncId);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}
