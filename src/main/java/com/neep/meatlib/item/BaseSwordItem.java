package com.neep.meatlib.item;

import com.neep.meatlib.Registries.ITEMRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

public class BaseSwordItem extends SwordItem implements MeatlibItem
{
    private final String registryName;

    public BaseSwordItem(String registryName, ToolMaterial material, int attackDamage, float speed, Settings settings)
    {
        super(material, attackDamage, speed, settings);
        this.registryName = registryName;
        ItemRegistry.queue(this);
    }

    @Override
    public @Nullable ItemGroup getGroupOverride()
    {
        return null;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
