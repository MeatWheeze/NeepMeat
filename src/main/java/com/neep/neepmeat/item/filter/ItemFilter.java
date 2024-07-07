package com.neep.neepmeat.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Objects;

public class ItemFilter implements Filter
{
    private final List<ItemVariant> items = DefaultedList.ofSize(10, ItemVariant.blank());
    private boolean useNbt = false;
    private boolean useDamage = false;

    public ItemFilter()
    {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList itemList = new NbtList();
        for (var item : items)
        {
            itemList.add(item.toNbt());
        }
        nbt.put("items", itemList);

        nbt.putBoolean("use_nbt", useNbt);
        nbt.putBoolean("use_damage", useDamage);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        NbtList itemList = nbt.getList("items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < itemList.size(); ++i)
        {
            items.set(i, ItemVariant.fromNbt(itemList.getCompound(i)));
        }

        this.useNbt = nbt.getBoolean("use_nbt");
        this.useDamage = nbt.getBoolean("use_damage");
    }

    public void setItem(int index, ItemVariant item)
    {
        if (index < 0 || index >= items.size())
            return;

        items.set(index, item);
    }

    public ItemVariant getItem(int index)
    {
        if (index < 0 || index >= items.size())
            return ItemVariant.blank();

        return items.get(index);
    }

    @Override
    public boolean matches(ItemVariant variant)
    {
        for (var item : items)
        {
            if (testItem(item, variant))
                return true;
        }
        return false;
    }

    private boolean testItem(ItemVariant filterItem, ItemVariant variant)
    {
        boolean itemsEqual = variant.getItem() == filterItem.getItem();
        if (!useNbt && !useDamage)
            return itemsEqual;

        NbtCompound filterNbt = filterItem.getNbt();
        NbtCompound nbt = variant.getNbt();
        int filterDamage = filterNbt == null ? 0 : filterNbt.getInt("Damage");
        int itemDamage = nbt == null ? 0 : nbt.getInt("Damage");
        if (useDamage && !useNbt)
        {
            return itemsEqual && filterDamage == itemDamage;
        }
        else
        {
            return itemsEqual && Objects.equals(filterNbt, nbt);
        }
    }

    @Override
    public Constructor<?> getType()
    {
        return Filters.ITEM;
    }

    public boolean ignoreDamage()
    {
        return useDamage;
    }

    public boolean ignoreNbt()
    {
        return useNbt;
    }

    public void setUseDamage(boolean useDamage)
    {
        this.useDamage = useDamage;
        if (!useDamage)
            this.useNbt = false;
    }

    public void setUseNbt(boolean useNbt)
    {
        this.useNbt = useNbt;
        if (useNbt)
            this.useDamage = true;
    }
}
