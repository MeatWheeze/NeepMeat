package com.neep.neepmeat.screen_handler;

import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.item.filter.FilterList;
import com.neep.neepmeat.screen_handler.slot.PatternSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class AdvancedRouterScreenHandler extends BasicScreenHandler
{
    private final List<FilterList> filters;

    // Client
    public AdvancedRouterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, DefaultedList.ofSize(6, new FilterList(0)));
    }

    // Server
    public AdvancedRouterScreenHandler(int syncId, PlayerInventory playerInventory, List<FilterList> filters)
    {
        super(ScreenHandlerInit.ADVANCED_ROUTER, playerInventory, null, syncId, null);
        this.filters = filters;

        createPlayerSlots(8, 95, playerInventory);
    }
}

