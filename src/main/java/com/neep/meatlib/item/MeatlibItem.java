package com.neep.meatlib.item;

import com.neep.meatlib.registry.SelfRegistrable;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface MeatlibItem extends SelfRegistrable
{
    @Override
    default void register(Identifier id)
    {
        Registry.register(Registries.ITEM, id, (Item) this);
    }

    String getRegistryName();
}
