package com.neep.neepmeat.block.multiblock;

import com.neep.neepmeat.blockentity.machine.VatControllerBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public abstract class PortBlockEntity<T extends TransferVariant<?>> extends IMultiBlock.Entity implements Storage<T>, IPortBlock.Entity
{
    private final Class<T> clazz;

    public PortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<T> clazz)
    {
        super(type, pos, state);
        this.clazz = clazz;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction)
    {
        if (hasController())
            return getController().getStorage(clazz).insert(resource, maxAmount, transaction);

        return 0;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction)
    {
        if (hasController())
            return getController().getStorage(clazz).extract(resource, maxAmount, transaction);

        return 0;
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction)
    {
        VatControllerBlockEntity be;
        if ((be = (VatControllerBlockEntity) getController()) != null)
        {
            return be.getStorage(clazz).iterator(transaction);
        }
        return Collections.emptyIterator();
    }
}
