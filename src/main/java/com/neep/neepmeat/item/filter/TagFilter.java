package com.neep.neepmeat.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.font.ReferenceFont;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TagFilter implements Filter
{
    @Nullable
    private TagKey<Item> tag;

    @Override
    public boolean matches(ItemVariant variant)
    {
        return variant.getItem().getRegistryEntry().streamTags().anyMatch(t -> Objects.equals(t, tag));
    }

    @Override
    public Constructor<?> getType()
    {
        return Filters.TAG;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        if (tag != null)
            nbt.putString("tag", tag.id().toString());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        if (nbt.contains("tag"))
        {
            Identifier id = Identifier.tryParse(nbt.getString("tag"));
            if (id != null)
                tag = TagKey.of(Registries.ITEM.getKey(), id);
            else
                tag = null;
        }
        else
        {
            tag = null;
        }
    }

    public void setTag(@Nullable TagKey<Item> tag)
    {
        this.tag = tag;
    }

    @Nullable
    public TagKey<Item> getTag()
    {
        return tag;
    }
}
