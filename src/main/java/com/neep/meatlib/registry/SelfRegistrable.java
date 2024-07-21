package com.neep.meatlib.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface SelfRegistrable
{
    void register(Identifier id);

    static SelfRegistrable ofItem(Item item)
    {
        return id -> Registry.register(Registries.ITEM, id, item);
    }

    static SelfRegistrable ofBlock(Block block)
    {
        return id -> Registry.register(Registries.BLOCK, id, block);
    }

    @FunctionalInterface
    interface PathProcessor extends Object2ObjectFunction<String, String>
    {
        @Override
        default String get(Object o)
        {
            return apply((String) o);
        }

        @Override
        String apply(String key);
    }
}
