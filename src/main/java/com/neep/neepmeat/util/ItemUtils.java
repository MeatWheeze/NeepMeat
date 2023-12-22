package com.neep.neepmeat.util;

import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.item.FluidComponentItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
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

    public static void scatterItems(World world, BlockPos pos, Storage<ItemVariant> storage)
    {
        Transaction transaction = Transaction.openOuter();
        Iterator<? extends StorageView<ItemVariant>> it = storage.iterator(transaction);
        while (it.hasNext())
        {
            StorageView<ItemVariant> view = it.next();
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, view.getResource().toStack((int) view.getAmount()));
        }
        transaction.commit();
    }

    public static void singleVariantInteract(PlayerEntity player, Hand hand, SingleVariantStorage<ItemVariant> storage)
    {
        ItemStack stack = player.getStackInHand(hand);
        try (Transaction transaction = Transaction.openOuter())
        {
            if ((storage.isResourceBlank() || storage.getResource().matches(stack)) && !stack.isEmpty())
            {
                long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                transaction.commit();
            }
            else if ((stack.isEmpty() || !storage.getResource().matches(stack)) && !storage.isResourceBlank())
            {
                ItemStack giveStack = storage.getResource().toStack((int) storage.getAmount());
                player.giveItemStack(giveStack);
                storage.extract(storage.getResource(), storage.getAmount(), transaction);
                transaction.commit();
            }
            else transaction.abort();
        }
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

    public static boolean playerHoldingPipe(PlayerEntity player, Hand hand)
    {
        return player.getStackInHand(hand).getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof IFluidPipe
                || blockItem instanceof FluidComponentItem);
    }
}
