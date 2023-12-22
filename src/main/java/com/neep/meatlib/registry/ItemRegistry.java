package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.IMeatItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry
{
    public static Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

    public static Item queueItem(String namespace, IMeatItem item)
    {
        MeatLib.assertActive(item);
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(new Identifier(namespace, item.getRegistryName()), (Item) item);
        return (Item) item;
    }

    public static Item queueItem(IMeatItem item)
    {
        MeatLib.assertActive(item);
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, item.getRegistryName()), (Item) item);
        return (Item) item;
    }

    public static Item queueItem(String path, Item item)
    {
        MeatLib.assertActive(item);
        ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, path), item);
        return item;
    }

    public static void flush()
    {
        for (Iterator<Map.Entry<Identifier, Item>> it = ITEMS.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<Identifier, Item> entry = it.next();
            // TODO: Remove the jank
            Registry.register(Registry.ITEM, entry.getKey(), entry.getValue());
            it.remove();
        }
        ITEMS.clear();
    }
}
