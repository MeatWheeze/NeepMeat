package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.meatlib.util.MeatlibItemGroups;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry
{
    public static Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

    public static Item queue(String namespace, MeatlibItem item)
    {
        MeatLib.assertActive(item);
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(new Identifier(namespace, item.getRegistryName()), (Item) item);
        return (Item) item;
    }

    public static <T extends Item & MeatlibItem> T queue(T item)
    {
        MeatLib.assertActive(item);
        ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, item.getRegistryName()), (Item) item);
        return item;
    }

    public static Item queue(String path, Item item)
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
            Registry.register(Registries.ITEM, entry.getKey(), entry.getValue());

            ItemGroup group = ((MeatlibItemExtension) entry.getValue()).meatlib$getItemGroup();
            if (group != null)
            {
                MeatlibItemGroups.add(group, entry.getValue());
            }

            it.remove();
        }
        ITEMS.clear();
    }
}
