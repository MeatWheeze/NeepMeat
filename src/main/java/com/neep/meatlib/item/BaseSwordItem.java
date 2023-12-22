package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class BaseSwordItem extends SwordItem implements IMeatItem
{
    private final String registryName;

    public BaseSwordItem(String registryName, ToolMaterial material, int attackDamage, float speed, Settings settings)
    {
        super(material, attackDamage, speed, settings);
        this.registryName = registryName;
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
