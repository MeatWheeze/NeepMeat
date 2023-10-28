package com.neep.meatlib.registry;

import com.mojang.serialization.Codec;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class ParticleRegistry
{
    public static DefaultParticleType register(String namespace, String id, DefaultParticleType particleType)
    {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(namespace, id), particleType);
    }

    public static <T extends ParticleEffect> ParticleType<T> register(String namespace, String id, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(namespace, id), new ParticleType<T>(false, factory)
        {

            @Override
            public Codec<T> getCodec()
            {
                return function.apply(this);
            }
        });
    }
}
