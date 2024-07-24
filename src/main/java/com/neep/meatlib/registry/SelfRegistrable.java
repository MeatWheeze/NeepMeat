package com.neep.meatlib.registry;

import com.neep.meatlib.util.MeatlibItemGroups;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface SelfRegistrable
{
    void register(Identifier id);

    static SelfRegistrable ofItem(Item item)
    {
        return id -> registerItem(id, item);
    }

    static SelfRegistrable ofBlock(Block block)
    {
        return id -> registerBlock(id, block);
    }

    static void registerItem(Identifier id, Item item)
    {
        Registry.register(Registries.ITEM, id, item);

        // Jank handling
        ItemRegistry.REGISTERED_ITEMS.add(item);

        ItemGroup group = (item).meatlib$getItemGroup();
        if (group != null)
        {
            MeatlibItemGroups.add(group, item);
        }
    }

    static void registerBlock(Identifier id, Block block)
    {
        // Jank handling
        BlockRegistry.REGISTERED_BLOCKS.add(block);

        Registry.register(Registries.BLOCK, id, block);
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
