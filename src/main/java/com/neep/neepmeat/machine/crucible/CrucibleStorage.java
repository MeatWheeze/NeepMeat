package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.inventory.ImplementedInventory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class CrucibleStorage implements NbtSerialisable, ImplementedInventory
{
    protected SyncableBlockEntity parent;

    protected WritableSingleFluidStorage fluidStorage;

    public CrucibleStorage(SyncableBlockEntity parent)
    {
        this.parent = parent;
        this.fluidStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET, parent::sync);
    }

    public SingleSlotStorage<FluidVariant> getStorage(Direction direction)
    {
        return fluidStorage;
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        throw new UnsupportedOperationException("CrucibleStorage is not a real inventory");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        fluidStorage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        fluidStorage.readNbt(nbt);
    }
}
