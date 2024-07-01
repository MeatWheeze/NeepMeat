package com.neep.neepmeat.machine.small_compressor;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class SmallCompressorScreenHandler extends BasicScreenHandler
{
    public SmallCompressorScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(playerInventory, new SimpleInventory(1), syncId, new ArrayPropertyDelegate(2));
    }

    public SmallCompressorScreenHandler(PlayerInventory playerInventory, Inventory inventory, int syncId, PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.SMALL_COMPRESSOR, playerInventory, inventory, syncId, delegate);
        checkSize(inventory, 1);

        this.addSlot(new Slot(inventory, 0, 80, 52)
        {
            @Override
            public boolean canInsert(ItemStack stack)
            {
                Integer entry = FuelRegistry.INSTANCE.get(stack.getItem());
                return entry != null && entry > 0;
            }
        });
        createPlayerSlots(8, 91 + 8, playerInventory);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
