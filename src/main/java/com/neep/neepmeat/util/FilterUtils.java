package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class FilterUtils
{
    @FunctionalInterface
    public interface Filter
    {
        boolean test(long i1, long i2);
    }

    public static Predicate<ItemVariant> containsVariant(List<ItemVariant> list)
    {
        return list::contains;
    }

    public static Predicate<StorageView<ItemVariant>> matchVariant(List<StorageView<ItemVariant>> list)
    {
        return view -> ItemUtils.containsResource(list, view.getResource());
    }

    public static Predicate<StorageView<ItemVariant>> matchOperator(List<StorageView<ItemVariant>> list, Filter filter)
    {
        return stack -> ItemUtils.contains(list, stack, filter);
    }

    public static boolean any(TransferVariant<?> variant)
    {
        return true;
    }

}
