package com.neep.meatlib.item;

import net.minecraft.item.Item;

/**
 * This was a bloody stupid idea.
 */
public class TieredCraftingItemFactory
{
    protected final String[] prefixes;

    public TieredCraftingItemFactory(String[] prefixes)
    {
        this.prefixes = prefixes;
    }

    public Void create(String registryName, boolean hasLore, Item.Settings settings)
    {
        for (String prefix : prefixes)
        {
            BaseCraftingItem item = new BaseCraftingItem(registryName + "_" + prefix, hasLore, settings);
        }
        return null;
    }
}
