package com.neep.meatlib.registry;

import com.neep.meatlib.registry.annotation.Path;
import com.neep.neepmeat.machine.live_machine.block.CrusherSegmentBlock;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An object that holds {@link SelfRegistrable}s before they are registered.
 * Only create one {@link RegistrationContext} per storage class.
 */
public class RegistrationContext
{
    private final String namespace;
    private final Set<Class<?>> validClasses;

//    private final Multimap<Object, SelfRegistrable> subEntries = Multimaps.newListMultimap(new IdentityHashMap<>(), ObjectArrayList::new);
//    private final Multimap<Object, Pair<SelfRegistrable.PathProcessor, SelfRegistrable>> processedSubEntries = Multimaps.newListMultimap(new IdentityHashMap<>(), ObjectArrayList::new);
//    private final List<Pair<Identifier, SelfRegistrable>> entries = new ObjectArrayList<>();
    private final List<RootNode> rootNodes = new ObjectArrayList<>();
    private final Map<Object, Node> allNodes = new IdentityHashMap<>();

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
        String path = field.getName().toLowerCase();
        @Nullable Path customPath = field.getAnnotation(Path.class);
        if (customPath != null)
            path = customPath.value();

        Identifier id = new Identifier(namespace, path);

        addParent(id, object);

//        for (var entry : processedSubEntries.get(object))
//        {
//            entries.add(Pair.of(new Identifier(namespace, entry.key().apply(path)), entry.value()));
//            processedSubEntries.removeAll(object);
//        }
//
//        for (var entry : subEntries.get(object))
//        {
//            entries.add(Pair.of(new Identifier(namespace, path), entry));
//            subEntries.removeAll(object);
//        }
    }

    public <T> T addParent(Identifier id, T object)
    {
        if (object instanceof CrusherSegmentBlock bdb)
        {
            System.out.println(id);
        }

        SelfRegistrable registrable = toRegistrable(object);
        if (registrable != null)
        {
            RootNode node = new RootNode(registrable, id.getPath());
            allNodes.put(object, node);
            rootNodes.add(node);
        }
        return object;
    }

    private static @Nullable SelfRegistrable toRegistrable(Object object)
    {
        if (object instanceof SelfRegistrable selfRegistrable)
            return selfRegistrable;

        if (object instanceof Item item)
            return SelfRegistrable.ofItem(item);

        if (object instanceof Block block)
            return SelfRegistrable.ofBlock(block);

        return null;
    }

    /**
     * For use when the key and entry may not be equal.
     * @param key The object that the {@link SelfRegistrable} is extracted from. May not be the same as entry.
     */
    private <T extends SelfRegistrable> T append(Object object, Object key, T entry, SelfRegistrable.PathProcessor processor)
    {
        Node node = allNodes.get(object).addChild(new Node(entry, processor));
        allNodes.put(key, node);
        return entry;
    }

    /**
     * For use when the key and are equal.
     */
    public <T extends SelfRegistrable> T append(Object object, T entry)
    {
        return append(object, entry, entry, s -> s);
    }

    public <T extends SelfRegistrable> T appendItem(Object object, T entry)
    {
        return append(object, entry);
    }

    /**
     * Extracts a SelfRegistrable from the given entry.
     */
    public <T> T append(Object object, T entry)
    {
        @Nullable SelfRegistrable registrable = toRegistrable(entry);
        if (registrable != null)
            append(object, (SelfRegistrable) entry);

        return entry;
    }

    public <T extends SelfRegistrable> T append(Object object, T entry, SelfRegistrable.PathProcessor processor)
    {
        return append(object, entry, entry, processor);
    }

    public <T extends SelfRegistrable> T append(Object object, T entry, String path)
    {
        return append(object, entry, s -> path);
    }

    public boolean isValidClass(Class<?> f)
    {
        for (var clazz : validClasses)
        {
            if (clazz.isAssignableFrom(f))
                return true;
        }
        return false;
    }

    public void registerAll()
    {
//        if (!processedSubEntries.isEmpty())
//        {
//            NeepMeat.LOGGER.error("Some processed sub-entries for {} were not consumed: {}",
//                    namespace,
//                    processedSubEntries.values().stream().map(Pair::value).collect(Collectors.toList()));
//        }
//
//        if (!subEntries.isEmpty())
//        {
//            NeepMeat.LOGGER.error("Some normal sub-entries for {} were not consumed: {}",
//                    namespace,
//                    new ArrayList<>(subEntries.values()));
//        }

//        entries.stream()
//                .distinct() // The previous registration solution allowed things to be queued more than once
//                .forEach(e ->
//        {
//            e.value().register(e.key());
//        });

        rootNodes.forEach(node -> node.traverse(namespace));
    }

    static class Node
    {
        protected final SelfRegistrable registrable;
        protected final SelfRegistrable.PathProcessor processor;
        protected final List<Node> children = new ObjectArrayList<>();

        public Node(SelfRegistrable registrable, SelfRegistrable.PathProcessor processor)
        {
            this.registrable = registrable;
            this.processor = processor;
        }

        public Node(SelfRegistrable registrable)
        {
            this.registrable = registrable;
            this.processor = s -> s;
        }

        public void traverse(String namespace, String path)
        {
            String currentPath = processor.apply(path);
            registrable.register(new Identifier(namespace, currentPath));

            for (var child : children)
            {
                child.traverse(namespace, currentPath);
            }
        }

        public Node addChild(Node node)
        {
            children.add(node);
            return node;
        }
    }

    static class RootNode extends Node
    {
        private final String path;

        public RootNode(SelfRegistrable registrable, String path)
        {
            super(registrable);
            this.path = path;
        }

        public void traverse(String namespace)
        {
            registrable.register(new Identifier(namespace, path));

            for (var child : children)
            {
                child.traverse(namespace, path);
            }
        }
    }
}
