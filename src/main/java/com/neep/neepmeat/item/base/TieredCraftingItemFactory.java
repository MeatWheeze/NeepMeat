package com.neep.neepmeat.item.base;

import net.minecraft.item.Item;

public class TieredCraftingItemFactory
{
    protected final String[] prefixes;

    public TieredCraftingItemFactory(String[] prefixes)
    {
        this.prefixes = prefixes;
    }

    public Void get(String registryName, Item.Settings settings)
    {
        for (String prefix : prefixes)
        {
            BaseCraftingItem item = new BaseCraftingItem(registryName + "_" + prefix, settings);
        }
        return null;
    }
}
