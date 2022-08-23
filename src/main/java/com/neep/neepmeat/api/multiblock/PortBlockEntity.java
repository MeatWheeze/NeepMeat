package com.neep.neepmeat.api.multiblock;

import com.neep.neepmeat.block.entity.machine.VatControllerBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public abstract class PortBlockEntity<T extends TransferVariant<?>> extends IMultiBlock.Entity implements Storage<T>, IPortBlock.Entity
{
    private final Class<T> resourceClazz;

    public PortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<T> clazz)
    {
        super(type, pos, state);
        this.resourceClazz = clazz;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction)
    {
        if (hasController())
            return getController().getStorage(resourceClazz).insert(resource, maxAmount, transaction);

        return 0;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction)
    {
        if (hasController())
            return getController().getStorage(resourceClazz).extract(resource, maxAmount, transaction);

        return 0;
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction)
    {
        VatControllerBlockEntity be;
        if ((be = (VatControllerBlockEntity) getController()) != null)
        {
            return (Iterator<StorageView<T>>) be.getStorage(resourceClazz).iterator(transaction);
        }
        return Collections.emptyIterator();
    }
}
