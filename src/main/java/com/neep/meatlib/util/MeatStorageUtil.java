package com.neep.meatlib.util;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class MeatStorageUtil
{
    @Nullable
    public static <T> ResourceAmount<T> findExtractableContent(@Nullable Storage<T> storage, BiPredicate<TransactionContext, T> filter, @Nullable TransactionContext transaction)
    {
        T extractableResource = findExtractableResource(storage, filter, transaction);

        if (extractableResource != null)
        {
            long extractableAmount = storage.simulateExtract(extractableResource, Long.MAX_VALUE, transaction);

            if (extractableAmount > 0)
            {
                return new ResourceAmount<>(extractableResource, extractableAmount);
            }
        }

        return null;
    }

    @Nullable
    public static <T> T findExtractableResource(@Nullable Storage<T> storage, BiPredicate<TransactionContext, T> filter, @Nullable TransactionContext transaction)
    {
        if (storage == null) return null;

        try (Transaction nested = Transaction.openNested(transaction))
        {
            for (StorageView<T> view : storage.iterable(nested))
            {
                // Extract below could change the resource, so we have to query it before extracting.
                T resource = view.getResource();

                if (!view.isResourceBlank() && filter.test(nested, resource) && view.extract(resource, Long.MAX_VALUE, nested) > 0)
                {
                    // Will abort the extraction.
                    return resource;
                }
            }
        }

        return null;
    }
}
