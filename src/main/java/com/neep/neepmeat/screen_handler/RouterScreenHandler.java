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

public class RouterScreenHandler extends BasicScreenHandler
{
    // Client
    public RouterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(18));
    }

    // Server
    public RouterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        super(ScreenHandlerInit.ROUTER, playerInventory, inventory, syncId, null);
        checkSize(inventory, 18);


        int m;
        int l;
        //Our inventory
        createSlots();
        //The player inventory
        createPlayerSlots(8, 95, playerInventory);
    }

    protected void createSlots()
    {
        int m, l;
        for (m = 0; m < 6; ++m)
        {
            for (l = 0; l < 3; ++l)
            {
                this.addSlot(new Slot(inventory, l + m * 3, 13 + m * (18 + 9), 19 + l * 18));
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

