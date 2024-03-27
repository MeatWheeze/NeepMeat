package com.neep.neepmeat.datagen.tag;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class NMTags
{
    public static final TagKey<Item> CHARNEL_COMPACTOR = registerItem(NeepMeat.NAMESPACE, "charnel_substrate");
    public static final TagKey<Item> BLOOD_BUBBLE_LOGS = registerItem(NeepMeat.NAMESPACE, "blood_bubble_logs");
    public static final TagKey<Item> FLUID_PIPES = registerItem(NeepMeat.NAMESPACE, "fluid_pipes");
    public static final TagKey<Item> RAW_MEAT = TagKey.of(Registries.ITEM.getKey(), new Identifier("c", "raw_meat"));
    public static final TagKey<Item> METAL_SCAFFOLDING = TagKey.of(Registries.ITEM.getKey(), new Identifier("c", "metal_scaffolding"));
    public static final TagKey<Item> GUIDE_LOOKUP = TagKey.of(Registries.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "guide_lookup"));

    public static final TagKey<EntityType<?>> CLONEABLE = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier(NeepMeat.NAMESPACE, "cloneable"));

    private static TagKey<Item> registerItem(String namespace, String id)
    {
        return TagKey.of(Registries.ITEM.getKey(), new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
