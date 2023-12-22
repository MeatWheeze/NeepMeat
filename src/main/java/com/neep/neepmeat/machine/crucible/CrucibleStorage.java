package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class CrucibleStorage implements NbtSerialisable, ImplementedRecipe.DummyInventory
{
    protected CrucibleBlockEntity parent;

    protected WritableSingleFluidStorage fluidStorage;

    protected SingleVariantStorage<ItemVariant> itemStorage = new SingleVariantStorage<ItemVariant>()
    {
        @Override
        public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            return parent.processItem(insertedVariant, maxAmount, transaction);
        }

        @Override
        public long extract(ItemVariant extractedVariant, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        protected ItemVariant getBlankVariant()
        {
            return ItemVariant.blank();
        }

        @Override
        protected long getCapacity(ItemVariant variant)
        {
            return 1;
        };
    };

    public CrucibleStorage(CrucibleBlockEntity parent)
    {
        this.parent = parent;
        this.fluidStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET, parent::sync);
    }

    public SingleSlotStorage<FluidVariant> getStorage(Direction direction)
    {
        return fluidStorage;
    }

    public SingleSlotStorage<ItemVariant> getItemStorage(Direction direction)
    {
        return direction == Direction.UP ? itemStorage : null;
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
