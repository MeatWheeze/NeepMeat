package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.machine.assembler.AssemblerBlockEntity;
import com.neep.neepmeat.screen_handler.slot.PatternSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class AssemblerScreenHandler extends BasicScreenHandler
{
    public static final int ID_TOGGLE_SELECT = 0;

    protected Inventory dummyInventory = new SimpleInventory(12);
    protected Inventory targetInventory;
    protected int targetSize;

    // Client
    public AssemblerScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(28), new SimpleInventory(12), new ArrayPropertyDelegate(4));
    }

    // Server
    public AssemblerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, Inventory targetInventory, PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.ASSEMBLER, playerInventory, inventory, syncId, delegate);
        checkSize(inventory, 28);

        this.targetInventory = targetInventory;
        this.targetSize = targetInventory.size();

        createSlots();
        createPlayerSlots(28, 129, playerInventory);
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        propertyDelegate.set(2, 0); // Ensure that selection mode is disabled when closed
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack())
        {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.size())
            {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(originalStack, 24, this.inventory.size(), false))
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

    protected void createSlots()
    {
        createDisplaySlotBlock(8, 18, 3, 4, targetInventory, dummyInventory, 0, DisplaySlot::new);
        createSlotBlock(67, 18, 3, 4, inventory, 0, PatternSlot::new);
        createSlotBlock(127, 18, 3, 4, inventory, 12, Slot::new);
        createSlotBlock(191, 18, 1, 4, inventory, 24, OutputSlot::new);
    }

    protected void createDisplaySlotBlock(int startX, int startY, int nx, int ny, Inventory inventory, Inventory fallback, int startIndex, SlotConstructor constructor)
    {
        int m, l;
        for (m = 0; m < ny; ++m)
        {
            for (l = 0; l < nx; ++l)
            {
                int index = startIndex + l + m * nx;
                if (index < inventory.size())
                {
                    this.addSlot(constructor.construct(inventory, startIndex + l + m * nx, startX + l * 18, startY + m * 18));
                }
                else this.addSlot(constructor.construct(fallback, startIndex + l + m * nx, startX + l * 18, startY + m * 18));

            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        if (slotIndex > 12 && slotIndex < 24 && (
                actionType == SlotActionType.CLONE
//                || actionType == SlotActionType.PICKUP
                || actionType == SlotActionType.PICKUP_ALL
                || actionType == SlotActionType.QUICK_MOVE
                || actionType == SlotActionType.QUICK_CRAFT
                || actionType == SlotActionType.SWAP
                || actionType == SlotActionType.THROW))
            return;

        if (markOutput(slotIndex, button)) return;

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id)
    {
        if (id > 100)
        {
            return false;
        }
        if (id == ID_TOGGLE_SELECT)
        {
            // Cycle output slot selection mode
            int mode = propertyDelegate.get(2);
            propertyDelegate.set(2, mode > 0 ? 0 : 1);
        }

        return true;
    }

    protected boolean markOutput(int slot, int button)
    {
        if (slot >= 24 || propertyDelegate.get(2) == 0) return false; // Return if the clicked slot is outside the filter range
        int invIndex = slot % AssemblerBlockEntity.PATTERN_SLOTS;

        int prevSlots = propertyDelegate.get(0);
        // Shift 1 left to position corresponding to index, bitwise XOR to toggle bit.
        propertyDelegate.set(0, (1 << invIndex) ^ prevSlots);
        return true;
    }

    public static class DisplaySlot extends Slot
    {
        public DisplaySlot(Inventory inventory, int index, int x, int y)
        {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack)
        {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity)
        {
            return false;
        }
    }

    public static class OutputSlot extends Slot
    {
        public OutputSlot(Inventory inventory, int index, int x, int y)
        {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack)
        {
            return false;
        }
    }
}