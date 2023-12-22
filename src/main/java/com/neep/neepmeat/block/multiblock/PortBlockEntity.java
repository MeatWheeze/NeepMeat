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

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public abstract class PortBlockEntity<T extends TransferVariant<?>> extends IMultiBlock.Entity implements Storage<T>, IPortBlock.Entity
{
    public final StorageView<T> emptyView = new StorageView<>()
    {
        @Override
        public long extract(T resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public boolean isResourceBlank()
        {
            return true;
        }

        @Override
        public T getResource()
        {
            return null;
        }

        @Override
        public long getAmount()
        {
            return 0;
        }

        @Override
        public long getCapacity()
        {
            return 0;
        }
    };

    private final Class<T> clazz;

    protected VatControllerBlockEntity controller;

    public PortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<T> clazz)
    {
        super(type, pos, state);
        this.clazz = clazz;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction)
    {
        return getController().getStorage(clazz).insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction)
    {
        return getController().getStorage(clazz).extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction)
    {
        VatControllerBlockEntity be;
        if ((be = getController()) != null)
        {
            return be.getStorage(clazz).iterator(transaction);
        }
        return SingleViewIterator.create(emptyView, transaction);
    }

    @Override
    public VatControllerBlockEntity getController()
    {
        return controller;
    }
}
