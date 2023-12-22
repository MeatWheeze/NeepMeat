package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class ItemUtils
{
    public static boolean containsStack(List<ItemStack> list, ItemStack stack)
    {
        for (ItemStack itemStack : list)
        {
            if (ItemStack.areEqual(stack, itemStack))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean containsResource(List<StorageView<ItemVariant>> list, ItemVariant stack)
    {
        for (StorageView<ItemVariant> patternStack : list)
        {
            if (patternStack.getResource().equals(stack))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(List<StorageView<ItemVariant>> list, StorageView<ItemVariant> observedView, FilterUtils.Filter filter)
    {
        for (StorageView<ItemVariant> patternStack : list)
        {
            if (patternStack.getResource().equals(observedView.getResource())
                    && filter.test(observedView.getAmount(), patternStack.getAmount()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean notBlank(ItemStack stack)
    {
        return !stack.isEmpty();
    }

    public static boolean notBlank(StorageView<ItemVariant> view)
    {
        return !view.isResourceBlank();
    }

    public static ItemStack mutateView(StorageView<ItemVariant> view)
    {
        return view.getResource().toStack((int) view.getAmount());
    }

    public static <T extends TransferVariant<?>> Optional<Long> totalAmount(Storage<T> storage, T resource, Transaction transaction)
    {
        return StreamSupport.stream(storage.iterable(transaction).spliterator(), false)
                .filter(view -> view.getResource().equals(resource))
                .map(StorageView::getAmount)
                .reduce(Long::sum);
    }
}
