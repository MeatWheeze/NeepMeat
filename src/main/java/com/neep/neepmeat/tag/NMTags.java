package com.neep.neepmeat.tag;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class NMTags
{
    public static final Tag<Item> CHARNEL_COMPACTOR = register(NeepMeat.NAMESPACE, "charnel_substrate");

    private static Tag<Item> register(String namespace, String id)
    {
        return TagRegistry.item(new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
