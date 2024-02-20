package com.neep.neepmeat.client.particle;

import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class BloodDripParticleFactory implements ParticleFactory<DefaultParticleType>
{
    protected final SpriteProvider spriteProvider;
    private final Random random;

    public BloodDripParticleFactory(SpriteProvider spriteProvider)
    {
        this.spriteProvider = spriteProvider;
        this.random = Random.create();
    }

    public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
    {
        BloodDripParticle drip = new BloodDripParticle(clientWorld, d, e, f, NMFluids.STILL_BLOOD);
        drip.setSprite(this.spriteProvider);
        return drip;
    }

    @Environment(EnvType.CLIENT)
    private static class BloodDripParticle extends BlockLeakParticle
    {
//        private final ParticleEffect nextParticle;
        private int state = 0;

        public BloodDripParticle(ClientWorld world, double x, double y, double z, Fluid fluid)
        {
            super(world, x, y, z, fluid);
            this.gravityStrength *= 0.02F;
            this.maxAge = 40;
        }

        protected void updateAge()
        {
            if (this.maxAge-- <= 0)
            {
                state++;
                this.maxAge = 50;
            }
            if (state >= 2)
            {
                this.markDead();
            }
        }

        protected void updateVelocity()
        {
            if (state == 0)
            {
                this.velocityX *= 0.02;
                this.velocityY *= 0.02;
                this.velocityZ *= 0.02;
            }
            else
            {
                this.velocityMultiplier = 1;
                this.velocityY = -0.5;
            }


            if (this.onGround)
                markDead();

        }
    }
}
