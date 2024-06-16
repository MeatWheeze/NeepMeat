package com.neep.neepmeat.api.processing.random_ores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

public interface WeightModifier
{
    Registry<Codec<? extends WeightModifier>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(NeepMeat.NAMESPACE, "random_ore_modifiers")), Lifecycle.stable(), null);

    Codec<Codec<? extends WeightModifier>> CODEC = REGISTRY.getCodec();

    float apply(float base, World world, BlockPos pos);

    Codec<? extends WeightModifier> getCodec();
}
