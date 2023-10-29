package com.neep.meatlib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;

public interface IMeatItem
{
    // Stopgap measure for 1.19.2 backport
    @Nullable default ItemGroup getGroupOverride()
    {
        return MeatItemGroups.getGroup((Item) this);
    }

    String getRegistryName();

    default Item group(ItemGroup group)
    {
        MeatItemGroups.queueItem(group, (Item) this);
        return (Item) this;
    }
}
