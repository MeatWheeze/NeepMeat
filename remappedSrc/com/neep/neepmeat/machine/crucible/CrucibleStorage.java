package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class CrucibleStorage implements NbtSerialisable, ImplementedRecipe.DummyInventory
{
    protected CrucibleBlockEntity parent;
    protected WritableSingleFluidStorage fluidStorage;

    protected SingleVariantStorage<ItemVariant> itemStorage;

    public CrucibleStorage(CrucibleBlockEntity parent)
    {
        this.parent = parent;
        this.fluidStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET, parent::sync);

        this.itemStorage = new WritableStackStorage(parent::sync, 3)
        {
            @Override
            public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction)
            {
                // Sometimes empty item stack entities can appear
                if (insertedVariant.isBlank() || maxAmount == 0) return 0;

                long processed = 0;
                try (Transaction inner = transaction.openNested())
                {
                    long inserted = super.insert(insertedVariant, maxAmount, inner);
                    if (inserted > 0)
                    {
                        processed = parent.processItem(insertedVariant, inserted, inner);
                    }

                    if (processed > 0)
                        inner.commit();
                    else
                        inner.abort();
                }
                return processed;
            }

            @Override
            protected ItemVariant getBlankVariant()
            {
                return ItemVariant.blank();
            }
        };
    }

    public SingleSlotStorage<FluidVariant> getStorage(@Nullable Direction direction)
    {
        return fluidStorage;
    }

    public SingleSlotStorage<ItemVariant> getItemStorage(@Nullable Direction direction)
    {
        return direction == Direction.UP || direction == null ? itemStorage : null;
    }

    public CombinedStorage<FluidVariant, SingleVariantStorage<FluidVariant>> getFluidOutput()
    {
        return parent.getOutput();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        fluidStorage.writeNbt1(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        fluidStorage.readNbt(nbt);
    }
}
