package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;

public class BaseCraftingItem extends BaseItem implements NMItem
{
    private final String registryName;

    public BaseCraftingItem(String registryName, boolean hasLore, Settings settings)
    {
        super(registryName, hasLore, settings);
        this.registryName = registryName;
        ItemRegistry.queueItem(registryName, this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
