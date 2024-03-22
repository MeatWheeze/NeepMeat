package com.neep.neepmeat.machine.large_crusher;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;

public class LargeCrusherStorage implements IGrinderStorage, NbtSerialisable
{
    private final ImplementedInventory inputInventory = ImplementedInventory.ofSize(4);
    private final InventoryStorage inputStorage = InventoryStorage.of(inputInventory, null);

    private final ImplementedInventory outputInventory = ImplementedInventory.ofSize(8);
    private final InventoryStorage outputStorage = InventoryStorage.of(outputInventory, null);

    private final XpStorage xpStorage = new XpStorage();

    @Override
    public Storage<ItemVariant> getInputStorage()
    {
        return inputStorage;
    }

    @Override
    public Storage<ItemVariant> getOutputStorage()
    {
        return outputStorage;
    }

    @Override
    public Storage<ItemVariant> getExtraStorage()
    {
        return outputStorage;
    }

    @Override
    public XpStorage getXpStorage()
    {
        return xpStorage;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("input", inputInventory.writeNbt(new NbtCompound()));
        nbt.put("output", outputInventory.writeNbt(new NbtCompound()));
        nbt.put("xp", xpStorage.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        inputInventory.readNbt(nbt.getCompound("input"));
        outputInventory.readNbt(nbt.getCompound("output"));
        xpStorage.readNbt(nbt.getCompound("xp"));
    }
}
