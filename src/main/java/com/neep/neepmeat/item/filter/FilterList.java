package com.neep.neepmeat.item.filter;

import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.util.NbtSerialisable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class FilterList implements NbtSerialisable
{
    public static final ParamCodec<FilterList> CODEC = ParamCodec.of(FilterList.class,
        FilterList::write, FilterList::read);

    private final List<Entry> entries = new ObjectArrayList<>();

    public FilterList()
    {

    }

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
        buf.writeNbt(writeNbt(new NbtCompound()));
    }

    public static FilterList read(PacketByteBuf buf)
    {
        FilterList filterList = new FilterList();
        NbtCompound nbt = buf.readNbt();

        // Null check probably unnecessary
        if (nbt != null)
            filterList.readNbt(nbt);

        return filterList;
    }

    public List<Entry> getEntries()
    {
        return entries;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        for (Entry entry : entries)
        {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("id", Filter.REGISTRY.getId(entry.filter.getType()).toString());
            list.add(entry.filter.writeNbt(entryNbt));
        }
        nbt.put("entries", list);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        entries.clear();
        NbtList list = nbt.getList("entries", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            NbtCompound entryNbt = list.getCompound(i);
            Identifier id = Identifier.tryParse(entryNbt.getString("id"));
            Filter.Constructor<?> constructor = Filter.REGISTRY.get(id);
            if (constructor != null)
            {
                Entry entry = new Entry(constructor.create());
                entry.update(entryNbt);
                entries.add(entry);

            }
        }
    }

    public Filter getFilter(int i)
    {
        return entries.get(i).filter;
    }

    public static class Entry
    {
        private final Filter filter;

        public Entry(Filter filter)
        {
            this.filter = filter;
        }

        public boolean matches(ItemVariant variant)
        {
            return filter.matches(variant);
        }

        public void update(NbtCompound nbt)
        {
            filter.readNbt(nbt);
        }
    }
}
