package com.neep.meatlib.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.neep.meatlib.registry.annotation.Path;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class RegistrationContext
{
    private final String namespace;
    private final Set<Class<?>> validClasses;
    private final Multimap<Object, SelfRegisterable> subEntries = Multimaps.newListMultimap(new IdentityHashMap<>(), ObjectArrayList::new);
    private final Multimap<Object, Pair<SelfRegisterable.PathProcessor, SelfRegisterable>> processedSubEntries = Multimaps.newListMultimap(new IdentityHashMap<>(), ObjectArrayList::new);
    private final List<Pair<Identifier, SelfRegisterable>> entries = new ObjectArrayList<>();

    public RegistrationContext(String namespace)
    {
        this.namespace = namespace;
        this.validClasses = Set.of(Item.class, Block.class);
    }

    public String namespace()
    {
        return namespace;
    }

    public void collectField(String namespace, Object object, Field field)
    {
        if (!validClasses.contains(object.getClass()))
            return;

        String path = field.getName().toLowerCase();
        @Nullable Path customPath = field.getAnnotation(Path.class);
        if (customPath != null)
            path = customPath.value();

        Identifier id = new Identifier(namespace, path);

        SelfRegisterable selfRegisterable = toRegistrable(object);
        if (selfRegisterable != null)
            entries.add(Pair.of(id, selfRegisterable));

        for (var entry : processedSubEntries.get(object))
        {
            entries.add(Pair.of(new Identifier(namespace, entry.key().apply(path)), entry.value()));
        }

        for (var entry : subEntries.get(object))
        {
            entries.add(Pair.of(new Identifier(namespace, path), entry));
        }
    }

    private static @Nullable SelfRegisterable toRegistrable(Object object)
    {
        if (object instanceof SelfRegisterable selfRegisterable)
            return selfRegisterable;

        if (object instanceof Item item)
            return SelfRegisterable.ofItem(item);

        if (object instanceof Block block)
            return SelfRegisterable.ofBlock(block);

        return null;
    }

    public <T extends SelfRegisterable> T append(Object object, T entry)
    {
        subEntries.put(object, entry);
        return entry;
    }

    public <T> T append(Object object, T entry)
    {
        @Nullable SelfRegisterable registrable = toRegistrable(entry);
        if (registrable != null)
            subEntries.put(object, registrable);

        return entry;
    }

    public <T extends SelfRegisterable> T append(Object object, SelfRegisterable.PathProcessor processor, T entry)
    {
        processedSubEntries.put(object, Pair.of(processor, entry));
        return entry;
    }

    public <T extends SelfRegisterable> T append(Object object, T registerable, String path)
    {
        processedSubEntries.put(object, Pair.of(s -> path, registerable));
        return registerable;
    }
}
