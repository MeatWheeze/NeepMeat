package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;

public class BaseCraftingItem extends BaseItem implements IMeatItem
{
    private final String registryName;

    public BaseCraftingItem(String registryName, int loreLines, Settings settings)
    {
        super(registryName, loreLines, settings);
        this.registryName = registryName;
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
