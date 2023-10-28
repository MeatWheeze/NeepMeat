package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.inventory.ImplementedInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@SuppressWarnings("UnstableApiUsage")
public class InventoryDetectorInventory implements ImplementedInventory
{
    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    protected InventoryStorage inventoryStorage = InventoryStorage.of(this, null);

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return items;
    }

    public Storage<ItemVariant> getStorage()
    {
        return inventoryStorage;
    }
}
