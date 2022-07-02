package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class RouterScreenHandler extends ScreenHandler
{
    private final Inventory inventory;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public RouterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(18));
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public RouterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        super(ScreenHandlerInit.ROUTER, syncId);
        checkSize(inventory, 18);
        this.inventory = inventory;
        //some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);

        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int m;
        int l;
        //Our inventory
        createSlots();
        //The player inventory
        for (m = 0; m < 3; ++m)
        {
            for (l = 0; l < 9; ++l)
            {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 89 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m)
        {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 147));
        }
    }

    protected void createSlots()
    {
//        for (int m = 0; m < 6; ++m)
//        {
//            for (int l = 0; l < 3; ++l)
//            {
//                this.addSlot(new Slot(inventory, l * m, 13 + m * (18 + 9), 20 + l * 18));
//            }
//        }
        int m, l;
        for (m = 0; m < 6; ++m)
        {
            for (l = 0; l < 3; ++l)
            {
                this.addSlot(new Slot(inventory, l + m * 3, 13 + m * (18 + 9), 20 + l * 18));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
//        System.out.println(slotIndex);
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        return ItemStack.EMPTY;
//        ItemStack newStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(invSlot);
//        if (slot != null && slot.hasStack())
//        {
//            ItemStack originalStack = slot.getStack();
//            newStack = originalStack.copy();
//            if (invSlot < this.inventory.size())
//            {
//                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true))
//                {
//                    return ItemStack.EMPTY;
//                }
//            }
//            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false))
//            {
//                return ItemStack.EMPTY;
//            }
//
//            if (originalStack.isEmpty())
//            {
//                slot.setStack(ItemStack.EMPTY);
//            }
//            else
//            {
//                slot.markDirty();
//                System.out.println("oooooooo");
//            }
//        }
//
//        return newStack;
    }
}

