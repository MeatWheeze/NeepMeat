package com.neep.neepmeat.api.processing.random_ores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeTagWeightModifier implements WeightModifier
{
    public static final Codec<BiomeTagWeightModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                    TagKey.codec(RegistryKeys.BIOME).fieldOf("tag").forGetter(m -> m.tag),
                    Codec.FLOAT.fieldOf("value").forGetter(m -> m.value),
                    Function.CODEC.fieldOf("function").forGetter(m -> m.function)
                ).apply(instance, BiomeTagWeightModifier::new));

    private final TagKey<Biome> tag;
    private final float value;
    private final Function function;

    public BiomeTagWeightModifier(TagKey<Biome> tag, float value, Function function)
    {
        this.tag = tag;
        this.value = value;
        this.function = function;
    }

    @Override
    public float apply(float base, World world, BlockPos pos)
    {
        RegistryEntry<Biome> biome = world.getBiome(pos);

        if (biome.isIn(tag))
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
