package com.neep.neepmeat.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class FallingParticle extends SpriteBillboardParticle
{
    public FallingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
    {
        super(clientWorld, d, e, f, g, h, i);
        this.gravityStrength = 1.0f;
    }

    @Override
    public ParticleTextureSheet getType()
    {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider)
        {
            this.spriteProvider = spriteProvider;
        }
        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
        {
            var particle = new FallingParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(spriteProvider);
            return particle;
        }
    }
}
