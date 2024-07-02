package com.neep.meatweapons.item.filter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

import java.util.List;

public class FilterList implements Filter
{
    private final List<Entry> entries = new ObjectArrayList<>();

    @Override
    public boolean matches(ItemVariant variant)
    {
        for (var entry : entries)
        {
            if (entry.matches(variant))
                return true;
        }
        return false;
    }

    static class Entry implements Filter
    {
        private final Filter filter;
        private boolean inverted = false;

        public Entry(Filter filter)
        {
            this.filter = filter;
        }

        @Override
        public boolean matches(ItemVariant variant)
        {
            return filter.matches(variant);
        }
    }
}
