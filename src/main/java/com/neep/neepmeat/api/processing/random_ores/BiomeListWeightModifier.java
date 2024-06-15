package com.neep.neepmeat.api.processing.random_ores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeListWeightModifier implements WeightModifier
{
    public static final Codec<BiomeListWeightModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                    Codec.list(Identifier.CODEC).fieldOf("biomes").forGetter(m -> m.biomes),
                    Codec.FLOAT.fieldOf("value").forGetter(m -> m.value),
                    Function.CODEC.fieldOf("function").forGetter(m -> m.function)
                ).apply(instance, BiomeListWeightModifier::new));

    private final List<Identifier> biomes;
    private final Set<Identifier> biomeSet;
    private final float value;
    private final Function function;

    public BiomeListWeightModifier(List<Identifier> biomes, float value, Function function)
    {
        this.biomes = biomes;
        this.value = value;
        this.function = function;
        this.biomeSet = new HashSet<>(biomes);
    }

    @Override
    public float apply(float base, World world, BlockPos pos)
    {
        RegistryEntry<Biome> biome = world.getBiome(pos);
        RegistryKey<Biome> key = biome.getKey().orElse(null);

        if (key != null && biomeSet.contains(key.getValue()))
        {
            return switch (function)
            {
                case ADD -> base + value;
                case MUL -> base * value;
            };
        }

        return base;
    }

    @Override
    public Codec<? extends WeightModifier> getCodec()
    {
        return CODEC;
    }
}
