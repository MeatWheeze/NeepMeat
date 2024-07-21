package com.neep.meatlib.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface SelfRegisterable
{
    void register(Identifier id);

    static SelfRegisterable ofItem(Item item)
    {
        return id -> Registry.register(Registries.ITEM, id, item);
    }

    static SelfRegisterable ofBlock(Block block)
    {
        return id -> Registry.register(Registries.BLOCK, id, block);
    }

    @FunctionalInterface
    interface PathProcessor extends Object2ObjectFunction<String, String>
    {
    }
}
