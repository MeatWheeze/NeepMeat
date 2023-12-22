package com.neep.neepmeat.init;

import com.neep.meatlib.registry.ParticleRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.mixin.ParticleManagerMixin;
import com.neep.neepmeat.particle.SwirlingParticle;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMParticles
{
    public static final ParticleType<SwirlingParticleEffect> BLOCK_SWIRL = ParticleRegistry.register(NeepMeat.NAMESPACE, "block_swirl", SwirlingParticleEffect.PARAMETERS_FACTORY, SwirlingParticleEffect::createCodec);

    public static DefaultParticleType MEAT_SPLASH = FabricParticleTypes.simple();

    public static void init()
    {
        MEAT_SPLASH = ParticleRegistry.register(NeepMeat.NAMESPACE, "meat_splash", MEAT_SPLASH);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {
            ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) ->
            {
                registry.register(new Identifier(NeepMeat.NAMESPACE, "particle/meat_splash_0"));
                registry.register(new Identifier(NeepMeat.NAMESPACE, "particle/meat_splash_1"));
                registry.register(new Identifier(NeepMeat.NAMESPACE, "particle/meat_splash_2"));
            }));

            ParticleFactoryRegistry.getInstance().register(BLOCK_SWIRL, new SwirlingParticle.Factory());
            ParticleFactoryRegistry.getInstance().register(MEAT_SPLASH, FlameParticle.Factory::new);
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
