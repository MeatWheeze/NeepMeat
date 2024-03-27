package com.neep.meatlib.item;

import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;

public interface MeatlibItemExtension
{
    // Not sure if the namespace prefix is necessary.
    // I've forgotten which document described best practices.
    @Nullable
    default ItemGroup meatlib$getItemGroup()
    {
        return null;
    }

    default boolean meatlib$supportsGuideLookup()
    {
        return false;
    }
}
