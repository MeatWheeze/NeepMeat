package com.neep.meatlib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public interface IMeatItem
{
    String getRegistryName();

    default Item group(ItemGroup group)
    {
        MeatItemGroups.queueItem(group, (Item) this);
        return (Item) this;
    }
}
