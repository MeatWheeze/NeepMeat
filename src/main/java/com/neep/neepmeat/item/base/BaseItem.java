package com.neep.neepmeat.item.base;

import com.neep.neepmeat.item.NMItem;
import net.minecraft.item.Item;

public class BaseItem extends Item implements NMItem
{
    private final String registryName;

    public BaseItem(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
