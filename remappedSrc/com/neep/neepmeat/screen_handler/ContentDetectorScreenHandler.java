package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.machine.content_detector.InventoryDetectorBehaviour;
import com.neep.neepmeat.init.ScreenHandlerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ContentDetectorScreenHandler extends ScreenHandler
{
    private final Inventory inventory;
    public PropertyDelegate delegate;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public ContentDetectorScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(9), new ArrayPropertyDelegate(2));
    }

    public ContentDetectorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.CONTENT_DETECTOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, 9);
        this.inventory = inventory;
        this.delegate = delegate;

        inventory.onOpen(playerInventory.player);
        this.addProperties(delegate);

        int m;
        int l;
        //Our inventory
        for (m = 0; m < 3; ++m)
        {
            for (l = 0; l < 3; ++l)
            {
                this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
            }
        }
        //The player inventory
        for (m = 0; m < 3; ++m)
        {
            for (l = 0; l < 9; ++l)
            {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m)
        {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id)
    {
        if (id > 100)
        {
            return false;
        }
        InventoryDetectorBehaviour.cycleDelegate(id, this.delegate);
        return true;
    }

    @Override
    public void setProperty(int id, int value)
    {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack())
        {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size())
            {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false))
            {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty())
            {
                slot.setStack(ItemStack.EMPTY);
            }
            else
            {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public int getCountMode()
    {
        return delegate.get(InventoryDetectorBehaviour.DEL_COUNT);
    }

    public int getBehaviourMode()
    {
        return delegate.get(InventoryDetectorBehaviour.DEL_BEHAVIOUR);
    }
}

