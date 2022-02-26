package com.neep.neepmeat.item.base;

import net.minecraft.item.Item;

public class TieredCraftingItemFactory
{
    protected final String[] prefixes;

    public TieredCraftingItemFactory(String[] prefixes)
    {
        this.prefixes = prefixes;
    }

    public Void get(String registryName, boolean hasLore, Item.Settings settings)
    {
        for (String prefix : prefixes)
        {
            BaseCraftingItem item = new BaseCraftingItem(registryName + "_" + prefix, hasLore, settings);
        }
        return null;
    }
}
