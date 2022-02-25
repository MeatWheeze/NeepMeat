package com.neep.neepmeat.item.base;

import com.neep.neepmeat.init.ItemInit;
import com.neep.neepmeat.item.NMItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

public class BaseSwordItem extends SwordItem implements NMItem
{
    private final String registryName;

    public BaseSwordItem(String registryName, Settings settings)
    {
        super(ToolMaterials.GOLD, 3, 2, settings);
        this.registryName = registryName;
        ItemInit.putItem(getRegistryName(), this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
