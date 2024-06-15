package com.neep.neepmeat.api.processing.random_ores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface WeightModifier
{
    Registry<Codec<? extends WeightModifier>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(NeepMeat.NAMESPACE, "random_ore_modifiers")), Lifecycle.stable());

    Codec<Codec<? extends WeightModifier>> CODEC = REGISTRY.getCodec();

    float apply(float base, World world, BlockPos pos);

    Codec<? extends WeightModifier> getCodec();
}
