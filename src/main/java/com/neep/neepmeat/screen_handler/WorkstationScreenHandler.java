package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.client.screen.button.ResultSlot;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.inventory.CombinedInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class WorkstationScreenHandler extends BasicScreenHandler
{
    protected DummyScreenHandler dummyScreenHandler = new DummyScreenHandler(this::onContentChanged);
    protected CraftingInventory input;
    protected Inventory output;

    // Client
    public WorkstationScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, null, new SimpleInventory(1), new CombinedInventory(new SimpleInventory(9), new SimpleInventory(1)));
    }

    // Server
    public WorkstationScreenHandler(int syncId, PlayerInventory playerInventory, CraftingInventory craftingInventory, Inventory outputInventory, Inventory mainInv)
    {
        super(ScreenHandlerInit.WORKSTATION, playerInventory, mainInv, syncId, null);
        // CraftingInventory's constructor requires a non-null ScreenHandler, so it must be initialised after super()
        this.input = craftingInventory != null ? craftingInventory : new CraftingInventory(this, 3, 3);
        this.output = outputInventory;
        checkSize(input, 9);
        checkSize(output, 1);
        this.input.onOpen(playerInventory.player);
        this.output.onOpen(playerInventory.player);

        createSlots();
        createPlayerSlots(8, 96, playerInventory);
    }

    public void createSlots()
    {
        createSlotBlock(30, 17, 3, 3, input, 0, Slot::new);
        this.addSlot(new ResultSlot(playerInventory.player, input, output, 0, 124, 35));
    }
}
