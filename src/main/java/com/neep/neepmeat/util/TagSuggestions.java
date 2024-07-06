package com.neep.neepmeat.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.ArrayList;
import java.util.List;

public class TagSuggestions
{
    public static final TagSuggestions INSTANCE = new TagSuggestions();

    private boolean dirty = true;
    private final Trie<String, Identifier> trie = new PatriciaTrie<>();
    private Registry<Item> itemRegistry;

    public List<Identifier> get(String prefix)
    {
        if (dirty)
            generate();

        return new ArrayList<>(trie.prefixMap(prefix).values());
    }

    private void generate()
    {
        dirty = false;
        trie.clear();
        itemRegistry.streamTags().forEach(tag ->
        {
            trie.put(tag.id().toString(), tag.id());
        });
    }

    public static void init()
    {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) ->
        {
            INSTANCE.itemRegistry = registries.get(Registries.ITEM.getKey());
        });
    }
}
