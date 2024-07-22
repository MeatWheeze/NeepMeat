package com.neep.meatlib.registry;

import com.google.common.base.MoreObjects;
import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.annotation.Path;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * An object that holds {@link SelfRegistrable}s before they are registered.
 * Only create one {@link RegistrationContext} per storage class.
 */
public class RegistrationContext
{
    private final String namespace;
    private final Set<Class<?>> validClasses;
    private boolean registered = false;

    private final List<RootNode> rootNodes = new ObjectArrayList<>();
    private final Map<Object, Node> allNodes = new IdentityHashMap<>();

    public RegistrationContext(String defaultNamespace)
    {
        this.namespace = defaultNamespace;
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
    }

    public <T> T addParent(Identifier id, T object)
    {
        SelfRegistrable registrable = toRegistrable(object);
        if (registrable != null)
        {
            allNodes.compute(object, (o, n) ->
            {
                if (n instanceof RootNode rootNode)
                {
                    rootNode.setId(id);
                    return rootNode;
                }
                else // If node is null or is a normal node
                {
                    RootNode rootNode = new RootNode(n, registrable);
                    rootNode.setId(id);
                    rootNodes.add(rootNode);
                    return rootNode;
                }
            });
        }
        else
        {
            MeatLib.LOGGER.error("Cannot extract a SelfRegistrable from root object {} ", object);
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
     * @param parent
     * @param key The object that a {@link SelfRegistrable} is extracted from. May not be the same as entry.
     */
    private <T extends SelfRegistrable> T append(Object parent, Object key, T entry, SelfRegistrable.PathProcessor processor)
    {
        // Get or create the parent node
        Node parentNode = allNodes.get(parent);
        if (parentNode == null)
        {
            @Nullable SelfRegistrable rootRegistrable = toRegistrable(parent);
            if (rootRegistrable != null)
            {
                parentNode = new Node(rootRegistrable);
                allNodes.put(parent, parentNode);
            }
            else
            {
                MeatLib.LOGGER.error("Cannot extract a SelfRegistrable from root object {} ", parent);
                return entry;
            }
        }

        // Get or create the entry node
        Node node = allNodes.computeIfAbsent(key, k -> new Node(entry, processor));

        // Update the path processor if the node was created without context
        if (node.processor != processor)
            node.setProcessor(processor);

        parentNode.addChild(node);
        return entry;
    }

    /**
     * For use when the key and are equal.
     */
    public <T extends SelfRegistrable> T append(Object root, T entry)
    {
        return append(root, entry, entry, s -> s);
    }

    public <T extends SelfRegistrable> T appendItem(Object root, T entry)
    {
        return append(root, entry);
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
//            MeatLib.LOGGER.error("Some processed sub-entries for {} were not consumed: {}",
//                    namespace,
//                    processedSubEntries.values().stream().map(Pair::value).collect(Collectors.toList()));
//        }
//
//        if (!subEntries.isEmpty())
//        {
//            MeatLib.LOGGER.error("Some normal sub-entries for {} were not consumed: {}",
//                    namespace,
//                    new ArrayList<>(subEntries.values()));
//        }

//        entries.stream()
//                .distinct() // The previous registration solution allowed things to be queued more than once
//                .forEach(e ->
//        {
//            e.value().register(e.key());
//        });

        if (registered)
            throw new IllegalStateException(this + ": registerAll called more than once!");

        registered = true;

//        int count = 0;
//        for (var node : allNodes.values())
//        {
//            if (node.registrable instanceof SmoothTileBlock smb)
//            {
//                count++;
//            }
//        }
//        System.out.println("Count: " + count);

        rootNodes.forEach(node ->
        {
            node.traverse();
        });

    }

    public <T extends Block> T withItem(T block, ItemSettings itemSettings)
    {
        itemSettings.create(block, this, itemSettings);
        return block;
    }

    static class Node
    {
        protected final SelfRegistrable registrable;
        protected SelfRegistrable.PathProcessor processor;
        protected final Set<Node> children = new HashSet<>();

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

        public void setProcessor(SelfRegistrable.PathProcessor processor)
        {
            this.processor = processor;
        }

        @Override
        public int hashCode()
        {
            return registrable.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof Node node && node.registrable == registrable;
        }
    }

    static class RootNode extends Node
    {
        @Nullable private Identifier id;

        public RootNode(SelfRegistrable registrable)
        {
            super(registrable);
        }

        /**
         * Promotes a node to a root node.
         */
        public RootNode(@Nullable Node previous, SelfRegistrable registrable)
        {
            super(registrable);
            if (previous != null)
            {
                children.addAll(previous.children);
            }
        }

        public void setId(Identifier id)
        {
            this.id = id;
        }

        public void traverse()
        {

//            if (registrable instanceof ItemDuctBlock idb)
//            {
//                System.out.println("registering it");
//            }

            if (id == null)
            {
                MeatLib.LOGGER.error("Null root object path for " + registrable + " of class " + registrable.getClass() + " " + registrable.hashCode());
                return;
//                throw new IllegalStateException("Null root object path for " + registrable + " of class " + registrable.getClass() + " " + registrable.hashCode());
            }

            registrable.register(id);

            for (var child : children)
            {
                child.traverse(id.getNamespace(), id.getPath());
            }
        }

        @Override
        public String toString()
        {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .toString();
        }
    }
}
