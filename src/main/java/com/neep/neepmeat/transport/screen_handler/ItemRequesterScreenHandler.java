package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.transport.ItemTransport;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.H_GRID;
import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.W_GRID;

public class ItemRequesterScreenHandler extends BasicScreenHandler
{
    // Client
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(W_GRID * H_GRID));
    }

    // Server
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        super(ItemTransport.ITEM_REQUESTER_HANDLER, playerInventory, inventory, syncId, null);
        checkSize(inventory, 1);
        createPlayerSlots(8, 174, playerInventory);
        createSlots();
    }

    protected void createSlots()
    {
        int w_slot = 18;
        int h_slot = 18;
        for (int j = 0; j < H_GRID; ++j)
        {
            for (int i = 0; i < W_GRID; ++i)
            {
                this.addSlot(new Slot(inventory, i + j * W_GRID, 8 + i * w_slot, 8 + h_slot + j * w_slot));
            }
        }
    }
}