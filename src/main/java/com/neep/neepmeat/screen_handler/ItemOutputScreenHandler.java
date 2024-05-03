package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class ItemOutputScreenHandler extends BasicScreenHandler
{
    public ItemOutputScreenHandler(PlayerInventory playerInventory, Inventory inventory, int syncId)
    {
        super(ScreenHandlerInit.ITEM_OUTPUT, playerInventory, inventory, syncId, null);

        createSlotBlock(18, 16, 3, 3, inventory, 0, Slot::new);

        createPlayerSlots(8, 96, playerInventory);
    }

    public ItemOutputScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(playerInventory, new SimpleInventory(9), syncId);
    }
}
