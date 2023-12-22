package com.neep.meatlib.item;

import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;

public class MeatItemGroups
{
    protected static HashMap<ItemGroup, Queue<Item>> QUEUE = Maps.newHashMap();

    public static void init()
    {
        // 1.19.4 things
//        QUEUE.forEach((group, items) ->
//        {
//            ItemGroupEvents.modifyEntriesEvent(group).register(entries ->
//            {
//                for (var item : items)
//                {
//                    entries.add(item);
//                }
//            });
//        });
    }

    public static void queueItem(ItemGroup group, Item item)
    {
        QUEUE.computeIfAbsent(group, g -> new ArrayDeque<>()).add(item);
    }

    public static void queueItems(ItemGroup group, Item... items)
    {
        var queue = QUEUE.get(group);
        queue.addAll(Arrays.asList(items));
    }
}
