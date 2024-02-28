package com.neep.meatlib.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MeatlibItemGroups
{
    private static final Multimap<ItemGroup, Item> GROUPS = ArrayListMultimap.create();

    public static void add(ItemGroup group, Item value)
    {
        GROUPS.put(group, value);
    }

    public static void init()
    {
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) ->
        {
            if (GROUPS.containsKey(group))
            {
                GROUPS.get(group).stream().map(Item::getDefaultStack).forEach(entries::add);
            }
        });
    }
}
