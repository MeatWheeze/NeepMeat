package com.neep.neepmeat.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import oshi.driver.unix.Xrandr;

import java.util.Random;

public class SwirlingParticle extends SpriteBillboardParticle
{
    private final BlockPos blockPos;
    private final float sampleU;
    private final float sampleV;
    private final double radius;
    private final double originX;
    private final double originY;
    private final double originZ;
    private final double speed;
    private double angle;

    public SwirlingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, double radius, double angle, double speed, BlockState state)
    {
        this(world, x, y, z, velocityX, velocityY, velocityZ, radius, angle, speed, state, BlockPos.ofFloored(x, y, z));
    }

    public SwirlingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, double radius, double angle, double speed, BlockState state, BlockPos blockPos)
    {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.radius = radius;
        this.speed = speed;
        this.angle = random.nextDouble() * 2 * Math.PI;
        this.originX = x;
        this.originY = y;
        this.originZ = z;

        this.x = getRectX(originX, this.angle, radius);
        this.z = getRectZ(originZ, this.angle, radius);

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.blockPos = blockPos;
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.gravityStrength = 1.0f;
        this.red = 0.6f;
        this.green = 0.6f;
        this.blue = 0.6f;
        if (!state.isOf(Blocks.GRASS_BLOCK)) {
            int i = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, 0);
            this.red *= (float)(i >> 16 & 0xFF) / 255.0f;
            this.green *= (float)(i >> 8 & 0xFF) / 255.0f;
            this.blue *= (float)(i & 0xFF) / 255.0f;
        }
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    public static double getRectX(double x, double theta, double r)
    {
        return x + Math.cos(theta) * r;
    }

    public static double getRectZ(double z, double theta, double r)
    {
        return z + Math.sin(theta) * r;
    }

    @Override
    public void tick()
    {
        this.angle += speed;

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge)
        {
            this.markDead();
            return;
        }
//        this.velocityY -= 0.04 * (double)this.gravityStrength;
//        this.move(this.velocityX, this.velocityY, this.velocityZ);

        this.x = getRectX(originX, angle, radius);
        this.z = getRectZ(originZ, angle, radius);

//        if (dx != 0.0 || dy != 0.0 || dz != 0.0)
//        {
//            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
//            this.repositionFromBoundingBox();
//        }


        if (this.field_28787 && this.y == this.prevPosY)
        {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= this.velocityMultiplier;
        this.velocityY *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;
        if (this.onGround) {
            this.velocityX *= 0.7f;
            this.velocityZ *= 0.7f;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public int getBrightness(float tint)
    {
        int i = super.getBrightness(tint);
        if (i == 0 && this.world.isChunkLoaded(this.blockPos))
        {
            return WorldRenderer.getLightmapCoordinates(this.world, this.blockPos);
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SwirlingParticleEffect>
    {
        @Override
        public Particle createParticle(SwirlingParticleEffect effect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
        {
            BlockState blockState = effect.getBlockState();
            if (blockState.isAir() || blockState.isOf(Blocks.MOVING_PISTON))
            {
                return null;
            }
            Random rand = new Random(0);
            return new SwirlingParticle(clientWorld, d, e, f, g, h, i, effect.radius, 0, effect.speed, blockState);
        }
    }
}