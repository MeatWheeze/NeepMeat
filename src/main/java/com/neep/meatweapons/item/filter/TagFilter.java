package com.neep.meatweapons.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

import java.util.HashSet;
import java.util.Set;

class TagFilter implements Filter
{
    private final Set<TagKey<Item>> tags = new HashSet<>();

    @Override
    public boolean matches(ItemVariant variant)
    {
        return variant.getItem().getRegistryEntry().streamTags().anyMatch(tags::contains);
    }
}
