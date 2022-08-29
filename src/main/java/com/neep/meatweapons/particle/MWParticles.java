package com.neep.meatweapons.particle;

import com.neep.meatlib.registry.ParticleRegistry;
import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class MWParticles
{
    public static DefaultParticleType PLASMA_PARTICLE = FabricParticleTypes.simple();

    public static void init()
    {
        PLASMA_PARTICLE = ParticleRegistry.register(MeatWeapons.NAMESPACE, "plasma", PLASMA_PARTICLE);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient()
    {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) ->
        {
            registry.register(new Identifier(MeatWeapons.NAMESPACE, "particle/plasma"));
        }));

        ParticleFactoryRegistry.getInstance().register(PLASMA_PARTICLE, FlameParticle.Factory::new);
    }

}
