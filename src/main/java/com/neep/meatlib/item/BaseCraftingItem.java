package com.neep.meatlib.item;

import com.neep.meatlib.Registries.ITEMRegistry;

public class BaseCraftingItem extends BaseItem implements MeatlibItem
{
    private final String registryName;

    public BaseCraftingItem(String registryName, int loreLines, Settings settings)
    {
        super(registryName, TooltipSupplier.simple(loreLines), settings);
        this.registryName = registryName;
        ItemRegistry.queue(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
