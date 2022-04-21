package com.neep.neepmeat.util;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class FilterUtils
{
    @FunctionalInterface
    public interface Filter
    {
        boolean test(int i1, int i2);
    }

    public static Predicate<ItemStack> matchItem(List<ItemStack> list)
    {
        return stack -> ItemUtils.containsItem(list, stack);
    }

    public static Predicate<ItemStack> matchOperator(List<ItemStack> list, Filter filter)
    {
        return stack -> ItemUtils.contains(list, stack, filter);
    }
}
