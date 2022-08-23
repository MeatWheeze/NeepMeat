package com.neep.neepmeat.datagen.tag;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMTags
{
    public static final TagKey<Item> CHARNEL_COMPACTOR = registerItem(NeepMeat.NAMESPACE, "charnel_substrate");
    public static final TagKey<Item> BLOOD_BUBBLE_LOGS = registerItem(NeepMeat.NAMESPACE, "blood_bubble_logs");

    private static TagKey<Item> registerItem(String namespace, String id)
    {
//        return RequiredTagListRegistry.register(new Identifier(namespace, id), "charnel_substrate");
//        return .method_40092(Registry.BLOCK_KEY, new Identifier("fabric", id));
//        return class_6862.method_40092(Registry.BLOCK_KEY, new Identifier("fabric", id));
        return TagKey.of(Registry.ITEM_KEY, new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
