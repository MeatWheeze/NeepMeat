package com.neep.neepmeat.datagen.tag;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMTags
{
    public static final TagKey<Item> CHARNEL_COMPACTOR = registerItem(NeepMeat.NAMESPACE, "charnel_substrate");
    public static final TagKey<Item> BLOOD_BUBBLE_LOGS = registerItem(NeepMeat.NAMESPACE, "blood_bubble_logs");
    public static final TagKey<Item> FLUID_PIPES = registerItem(NeepMeat.NAMESPACE, "fluid_pipes");
    public static final TagKey<Item> RAW_MEAT = TagKey.of(Registry.ITEM.getKey(), new Identifier("c", "raw_meat"));
    public static final TagKey<Item> RAW_FISH = TagKey.of(Registry.ITEM.getKey(), new Identifier("c", "raw_fish"));
    public static final TagKey<Item> METAL_SCAFFOLDING = TagKey.of(Registry.ITEM.getKey(), new Identifier("c", "metal_scaffolding"));
    public static final TagKey<Item> ROUGH_CONCRETE = TagKey.of(Registry.ITEM.getKey(), new Identifier("neepmeat", "rough_concrete"));
    public static final TagKey<Item> SMOOTH_TILE = TagKey.of(Registry.ITEM.getKey(), new Identifier("neepmeat", "smooth_tile"));
    public static final TagKey<Item> PAINTED_CORRUGATED_ASBESTOS = TagKey.of(Registry.ITEM.getKey(), new Identifier("neepmeat", "painted_corrugated_asbestos"));
    public static final TagKey<Item> GUIDE_LOOKUP = TagKey.of(Registry.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "guide_lookup"));
    public static final TagKey<Item> RAW_ORES = registerItem("c", "raw_ores");

    public static final TagKey<Block> PHAGE_RAY_OVERRIDE = TagKey.of(Registries.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "phage_ray_override_hardness"));

    public static final TagKey<Block> BLOCK_CRUSHING_INPUTS = TagKey.of(Registry.BLOCK.getKey(), new Identifier(NeepMeat.NAMESPACE, "block_crushing_inputs"));
    public static final TagKey<Item> BLOCK_CRUSHING_OUTPUTS = TagKey.of(Registry.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "block_crushing_outputs"));

    public static final TagKey<EntityType<?>> CLONEABLE = TagKey.of(Registry.ENTITY_TYPE.getKey(), new Identifier(NeepMeat.NAMESPACE, "cloneable"));

    private static TagKey<Item> registerItem(String namespace, String id)
    {
        return TagKey.of(Registry.ITEM.getKey(), new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
