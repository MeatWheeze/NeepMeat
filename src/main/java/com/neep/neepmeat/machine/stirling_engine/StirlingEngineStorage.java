package com.neep.neepmeat.machine.stirling_engine;

import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineStorage
{
    protected WritableStackStorage fuelStorage;

    public StirlingEngineStorage(StirlingEngineBlockEntity parent)
    {
        this.fuelStorage = new WritableStackStorage(parent)
        {
            @Override
            public boolean canInsert(ItemVariant resource)
            {
                return FuelRegistry.INSTANCE.get(resource.getItem()) != null;
            }

            @Override
            public long extract(ItemVariant variant, long maxAmount, TransactionContext transaction)
            {
                ItemVariant currentVariant = this.getResource();
                Item remainder = currentVariant.getItem().getRecipeRemainder();

                long extracted = super.extract(variant, maxAmount, transaction);

                if (this.isEmpty() && remainder != null)
                {
                    this.insert(ItemVariant.of(remainder), 1, transaction);
                }
                return extracted;
            }
        };
    }

    public WritableStackStorage getFuelStorage(Direction direction)
    {
        return fuelStorage;
    }

    public int decrementFuel(TransactionContext transaction)
    {
        ItemVariant resource = fuelStorage.getResource();
        if (fuelStorage.isEmpty())
            return -1;

        if (fuelStorage.extract(resource, 1, transaction) == 1)
        {
            return FuelRegistry.INSTANCE.get(resource.getItem());
        }
        return -1;
    }

    public void writeNbt(NbtCompound nbt)
    {
        this.fuelStorage.writeNbt(nbt);
    }

    public void readNbt(NbtCompound nbt)
    {
        this.fuelStorage.readNbt(nbt);
    }
}
