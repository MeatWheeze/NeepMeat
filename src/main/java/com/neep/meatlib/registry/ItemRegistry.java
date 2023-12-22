package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.IMeatItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry
{
    public static Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

    public static Item queueItem(IMeatItem item, String path)
    {
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        return ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, path), (Item) item);
    }

    public static Item queueItem1(String path, Item item)
    {
        return ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, path), item);
    }

    public static void registerItems()
    {
        for (Map.Entry<Identifier, Item> entry : ITEMS.entrySet())
        {
            Registry.register(Registry.ITEM, entry.getKey(), entry.getValue());
        }
    }
}
