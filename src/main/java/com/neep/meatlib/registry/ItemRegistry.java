package com.neep.meatlib.registry;

import com.neep.neepmeat.NeepMeat;
import com.neep.meatlib.item.NMItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry
{
    public static Map<Identifier, NMItem> ITEMS = new LinkedHashMap<>();

    public static Item queueItem(String path, NMItem item)
    {
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        return (Item) ITEMS.put(new Identifier(NeepMeat.NAMESPACE, path), item);
    }

    public static void registerItems()
    {
        for (Map.Entry<Identifier, NMItem> entry : ITEMS.entrySet())
        {
            Registry.register(Registry.ITEM, entry.getKey(), (Item) entry.getValue());
        }
    }
}
