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
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        return ITEMS.put(new Identifier(namespace, item.getRegistryName()), (Item) item);
    }

    public static Item queueItem(IMeatItem item)
    {
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        return ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, item.getRegistryName()), (Item) item);
    }

    public static Item queueItem(String path, Item item)
    {
        return ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, path), item);
    }

    public static void registerItems()
    {
        for (Iterator<Map.Entry<Identifier, Item>> it = ITEMS.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<Identifier, Item> entry = it.next();
            // TODO: Remove the jank
            Registry.register(Registry.ITEM, new Identifier(MeatLib.CURRENT_NAMESPACE, entry.getKey().getPath()), entry.getValue());
            it.remove();
        }
    }
}
