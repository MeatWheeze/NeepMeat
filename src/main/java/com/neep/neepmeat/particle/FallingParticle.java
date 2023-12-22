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
//        this.velocityMultiplier = 10;

//        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * (double)0.4f;
//        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * (double)0.4f;
//        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * (double)0.4f;
//        double j = (Math.random() + Math.random() + 1.0) * (double)0.15f;
//        double k = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
//        this.velocityX = this.velocityX / k * j * (double)0.4f;
//        this.velocityY = this.velocityY / k * j * (double)0.4f + (double)0.1f;
//        this.velocityZ = this.velocityZ / k * j * (double)0.4f;
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
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
