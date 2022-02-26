package com.neep.neepmeat.item.base;

import com.neep.neepmeat.init.ItemInit;
import com.neep.neepmeat.item.NMItem;
import net.minecraft.item.Item;
import net.minecraft.structure.OceanMonumentGenerator;

public class BaseCraftingItem extends BaseItem implements NMItem
{
    private final String registryName;

    public BaseCraftingItem(String registryName, Settings settings)
    {
        super(registryName, settings);
        this.registryName = registryName;
        ItemInit.putItem(registryName, this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
