package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.machine.live_machine.block.entity.ItemOutputPortBlockEntity;
import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class ItemOutputScreenHandler extends BasicScreenHandler
{
    public ItemOutputScreenHandler(PlayerInventory playerInventory, Inventory inventory, int syncId, PropertyDelegate propertyDelegate)
    {
        super(ScreenHandlerInit.ITEM_OUTPUT, playerInventory, inventory, syncId, propertyDelegate);

        createSlotBlock(18, 16, 3, 3, inventory, 0, Slot::new);

        createPlayerSlots(8, 96, playerInventory);
    }

    public ItemOutputScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(playerInventory, new SimpleInventory(9), syncId, new ArrayPropertyDelegate(1));
    }

    public void setEject(boolean eject)
    {
        ScreenPropertyC2SPacket.Client.send(0, eject ? 1 : 0);
    }
}
