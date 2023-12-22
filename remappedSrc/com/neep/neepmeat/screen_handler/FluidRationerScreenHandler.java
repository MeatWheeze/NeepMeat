package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.slot.PatternSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import org.jetbrains.annotations.Nullable;

public class FluidRationerScreenHandler extends BasicScreenHandler
{
    public static int PROP_TARGET_AMOUNT = 0;
    public static int PROPERTIES = 1;

    private int initialValue;

    // Client
    public FluidRationerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(PROPERTIES));

        // Ensure the value is correct even before the PropertyDelegate syncs
        setProperty(PROP_TARGET_AMOUNT, buf.readVarInt());
    }

    // Server
    public FluidRationerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, @Nullable PropertyDelegate delegate)
    {
        super(ScreenHandlerInit.FLUID_RATIONER, playerInventory, inventory, syncId, delegate);
        checkSize(inventory, 1);

        createSlots();
        createPlayerSlots(8, 96, playerInventory);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        ItemStack oldStack = getStacks().get(invSlot);
//        super.transferSlot(player, invSlot);
//        if (invSlot < playerInventory.size())
//        {
//            inventory.setStack(0, playerInventory.getStack(invSlot));
//        }
        inventory.setStack(0, oldStack.copy());
        return ItemStack.EMPTY;
    }

    protected void createSlots()
    {
        this.addSlot(new PatternSlot(inventory, 0, 80, 34));
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}
