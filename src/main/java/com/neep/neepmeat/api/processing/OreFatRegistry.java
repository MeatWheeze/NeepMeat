package com.neep.neepmeat.api.processing;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class OreFatRegistry
{
    protected static Map<Item, Entry> ENTRIES = new HashMap<>();

    public static void init()
    {
        register(Items.RAW_IRON, 0xfedec8, Items.IRON_INGOT);
        register(Items.RAW_GOLD, 0xfaea2e, Items.GOLD_INGOT);
        register(Items.RAW_COPPER, 0x4fba98, Items.COPPER_INGOT);
    }

    public static void register(Item item, Integer col, Item result)
    {
        ENTRIES.put(item, new Entry(col, result));
    }

    public static Entry get(Item item)
    {
        return ENTRIES.get(item);
    }

    public record Entry(Integer col, Item result)
    {

    }
}
