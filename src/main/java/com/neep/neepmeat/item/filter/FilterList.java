package com.neep.neepmeat.item.filter;

import com.neep.meatlib.api.network.ParamCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public class FilterList implements Filter
{
    public static final ParamCodec<FilterList> CODEC = ParamCodec.of(FilterList.class,
        FilterList::write, FilterList::read);

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

    public void add(Filter filter)
    {
        entries.add(new Entry(filter));
    }

    public void remove(int i)
    {
        entries.remove(i);
    }

    public int size()
    {
        return entries.size();
    }

    public void write(PacketByteBuf buf)
    {

    }

    public static FilterList read(PacketByteBuf buf)
    {
        return new FilterList();
    }

    public List<Entry> getEntries()
    {
        return entries;
    }

    public static class Entry implements Filter
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
