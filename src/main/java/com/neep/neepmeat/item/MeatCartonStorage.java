package com.neep.neepmeat.item;

import com.neep.neepmeat.api.processing.MeatFluidHelper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class MeatCartonStorage implements InsertionOnlyStorage<FluidVariant>
{
    private final ContainerItemContext context;
    private final Item emptyItem;
    private final Item fullitem;
    private final Fluid insertableFluid;
    private final long insertableAmount;

    public MeatCartonStorage(ContainerItemContext context, Item emptyItem, Item fullitem, Fluid insertableFluid, long insertableAmount)
    {
        this.context = context;
        this.emptyItem = emptyItem;
        this.fullitem = fullitem;
        this.insertableFluid = insertableFluid;
        this.insertableAmount = insertableAmount;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (!context.getItemVariant().isOf(emptyItem)) return 0;

        if (resource.isOf(insertableFluid) && maxAmount >= insertableAmount)
        {
            NbtCompound nbt = MeatFluidHelper.copyRootRounded(new NbtCompound(), resource.getNbt());
            ItemVariant newVariant = ItemVariant.of(fullitem, nbt);

            if (context.exchange(newVariant, 1, transaction) == 1)
            {
                return insertableAmount;
            }
        }

        return 0;
    }

    @Override
    public Iterator<? extends StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return SingleViewIterator.create(new BlankVariantView<>(FluidVariant.blank(), insertableAmount), transaction);
    }
}
