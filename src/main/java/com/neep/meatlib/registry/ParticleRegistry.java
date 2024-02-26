package com.neep.meatlib.registry;

import com.mojang.serialization.Codec;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ParticleRegistry
{
    public static DefaultParticleType register(String namespace, String id, DefaultParticleType particleType)
    {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(namespace, id), particleType);
    }

    public static <T extends ParticleEffect> ParticleType<T> register(String namespace, String id, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(namespace, id), new ParticleType<T>(false, factory)
        {

            @Override
            public Codec<T> getCodec()
            {
                return function.apply(this);
            }
        });
    }
}
