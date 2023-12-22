package com.neep.meatlib.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeatureRegistry
{
    public static void registerFeature(String namespace, String path)
    {
        RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(namespace, path));
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key.getValue(), TREE_RICH);
    }
}
