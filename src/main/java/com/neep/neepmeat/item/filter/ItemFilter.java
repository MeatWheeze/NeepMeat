package com.neep.neepmeat.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class ItemFilter implements Filter
{
    private ItemVariant item = ItemVariant.blank();
    private boolean ignoreNbt = true;
    private boolean ignoreDamage = true;

    public ItemFilter()
    {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("item", item.toNbt());

        nbt.putBoolean("ignore_nbt", ignoreNbt);
        nbt.putBoolean("ignore_damage", ignoreDamage && !ignoreNbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.item = ItemVariant.fromNbt(nbt.getCompound("item"));

        this.ignoreNbt = nbt.getBoolean("ignore_nbt");
        this.ignoreDamage = nbt.getBoolean("ignore_damage");

        if (!ignoreNbt)
            ignoreDamage = false;
    }

    @Override
    public boolean matches(ItemVariant variant)
    {
        boolean itemsEqual = variant.getItem() == this.item.getItem();
        if (ignoreNbt && ignoreDamage)
            return itemsEqual;

        NbtCompound filterNbt = item.getNbt();
        NbtCompound nbt = variant.getNbt();
        int filterDamage = filterNbt == null ? 0 : filterNbt.getInt("Damage");
        int itemDamage = nbt == null ? 0 : nbt.getInt("Damage");
        if (ignoreNbt && !ignoreDamage)
        {
            return itemsEqual && filterDamage == itemDamage;
        }
        else
        {
            // ignoreDamage should never be true if ignoreNbt is true.
            return itemsEqual && Objects.equals(filterNbt, nbt);
        }
    }

    @Override
    public Constructor<?> getType()
    {
        return Filters.ITEM;
    }
}
