package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.Slot;

public class AlloyKilnScreenHandler extends BasicScreenHandler
{
    // Client
    public AlloyKilnScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(4), new ArrayPropertyDelegate(4));
    }

    // Server
    public AlloyKilnScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.ALLOY_KILN, playerInventory, inventory, syncId, delegate);
        checkSize(inventory, 4);

        createSlots();
        createPlayerSlots(8, 95, playerInventory);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }

    protected void createSlots()
    {
        this.addSlot(new FuelSlot(inventory, 0, 56, 53)); // Fuel
        this.addSlot(new Slot(inventory, 1, 45, 17)); // Left ingredient
        this.addSlot(new Slot(inventory, 2, 67, 17)); // Right Ingredient
        this.addSlot(new Slot(inventory, 3, 116, 35) // Output
        {
            @Override
            public boolean canInsert(ItemStack stack)
            {
                return false;
            }
        });
    }

    public static class FuelSlot extends Slot
    {
        public FuelSlot(Inventory inventory, int index, int x, int y)
        {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack)
        {
            return FuelRegistry.INSTANCE.get(stack.getItem()) != null || FurnaceFuelSlot.isBucket(stack);
        }

        @Override
        public int getMaxItemCount(ItemStack stack)
        {
            return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
        }
    }
}