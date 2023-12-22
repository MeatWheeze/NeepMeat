package com.neep.neepmeat;

import com.neep.neepmeat.item.BaseItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ItemInit
{
    public static List<Item> ITEMS = new ArrayList<>();

    public static void registerItems()
    {
        for (Item item : ITEMS)
        {
            Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, ((BaseItem) item).getItemName()), item);
        }
    }

}
