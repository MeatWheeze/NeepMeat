package com.neep.meatlib.item;

import com.neep.meatlib.registry.SelfRegistrable;
import com.neep.meatlib.util.MeatlibItemGroups;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface MeatlibItem extends SelfRegistrable
{
    @Override
    default void register(Identifier id)
    {
        Registry.register(Registries.ITEM, id, (Item) this);

        ItemGroup group = ((MeatlibItemExtension) this).meatlib$getItemGroup();
        if (group != null)
        {
            MeatlibItemGroups.add(group, (Item) this);
        }
    }
}
