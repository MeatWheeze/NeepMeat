package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

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

    public static boolean containsItem(List<ItemStack> list, ItemStack stack)
    {
        for (ItemStack patternStack : list)
        {
            if (ItemStack.areItemsEqual(stack, patternStack))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(List<ItemStack> list, ItemStack observedStack, FilterUtils.Filter filter)
    {
        for (ItemStack patternStack : list)
        {
            if (ItemStack.areItemsEqual(observedStack, patternStack) && filter.test(observedStack.getCount(), patternStack.getCount()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean validStack(ItemStack stack)
    {
        return !stack.isEmpty();
    }

    public static ItemStack mutateView(StorageView<ItemVariant> view)
    {
        return view.getResource().toStack((int) view.getAmount());
//        return Items.STONE.getDefaultStack();
    }
}
