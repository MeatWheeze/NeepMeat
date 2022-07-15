package com.neep.neepmeat.init;

import com.mojang.serialization.Codec;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.mixin.ParticleManagerMixin;
import com.neep.neepmeat.particle.SwirlingParticle;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class NMParticles
{
    public static final ParticleType<SwirlingParticleEffect> BLOCK_SWIRL = register(NeepMeat.NAMESPACE, "block_swirl", SwirlingParticleEffect.PARAMETERS_FACTORY, SwirlingParticleEffect::createCodec);


    public static void init()
    {
//            registerFactory(client.particleManager, BLOCK_SWIRL, SwirlingParticle.Factory::new);
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

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
//            ParticleFactoryRegistry.getInstance().register(BLOCK_SWIRL, SwirlingParticle.Factory::new);
//            ((ParticleManagerAccessor) MinecraftClient.getInstance().particleManager).getSpriteAwareFactories().put(Registry.PARTICLE_TYPE.getId(BLOCK_SWIRL), simpleSpriteProvider);
            ParticleFactoryRegistry.getInstance().register(BLOCK_SWIRL, new SwirlingParticle.Factory());
        }

        private static <T extends ParticleEffect> void registerFactory(ParticleType<T> type, SpriteAwareFactory<?> factory)
        {
            ParticleManager manager = MinecraftClient.getInstance().particleManager;
            ParticleManager.SimpleSpriteProvider simpleSpriteProvider = ParticleManagerMixin.invokeConstructor();
            ((ParticleManagerAccessor) manager).getSpriteAwareFactories().put(Registry.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
            ((ParticleManagerAccessor) manager).getFactories().put(Registry.PARTICLE_TYPE.getRawId(type), factory.create(simpleSpriteProvider));
        }

        @FunctionalInterface
        @Environment(value = EnvType.CLIENT)
        interface SpriteAwareFactory<T extends ParticleEffect>
        {
            ParticleFactory<T> create(SpriteProvider var1);
        }

//        @Environment(value = EnvType.CLIENT)
//        public static class SimpleSpriteProvider extends ParticleManager.SimpleSpriteProvider
//        {
//            private List<Sprite> sprites;
//
//            public static SimpleSpriteProvider create()
//            {
//                return ParticleManagerMixin.invokeConstructor();
//            }
//
//            @Override
//            public Sprite getSprite(int i, int j)
//            {
//                return this.sprites.get(i * (this.sprites.size() - 1) / j);
//            }
//
//            @Override
//            public Sprite getSprite(Random random)
//            {
//                return this.sprites.get(random.nextInt(this.sprites.size()));
//            }
//
//            public void setSprites(List<Sprite> sprites)
//            {
//                this.sprites = ImmutableList.copyOf(sprites);
//            }
//        }

        static
        {
            ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) ->
            {
//            registerFactory(client.particleManager, BLOCK_SWIRL, SwirlingParticle.Factory::new);
//                registerFactory(BLOCK_SWIRL, SwirlingParticle.Factory::new);
            }));
        }
    }
}
