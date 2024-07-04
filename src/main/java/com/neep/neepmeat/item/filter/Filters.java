package com.neep.neepmeat.item.filter;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Filters
{
    public static final Filter.Constructor<ItemFilter> ITEM = Registry.register(Filter.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "item"), ItemFilter::new);
    public static final Filter.Constructor<TagFilter> TAG = Registry.register(Filter.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "tag"), TagFilter::new);

    public static void init()
    {
    }
}
