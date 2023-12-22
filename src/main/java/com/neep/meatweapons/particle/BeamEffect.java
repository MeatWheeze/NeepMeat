package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class BeamEffect extends GraphicsEffect
{
    public static final Identifier BEAM_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/beam.png");
    public static final RenderLayer BEAM_LAYER = RenderLayer.getEntityTranslucent(BEAM_TEXTURE);

    public BeamEffect(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        super(world, start, end, velocity, scale, maxTime);
    }

    public void tick()
    {
        super.tick();

        if (maxTime > 0 && time > maxTime)
        {
            this.remove();
        }

        Random random = new Random(time);
        world.addParticle(ParticleTypes.ENCHANTED_HIT, end.x, end.y, end.z, random.nextFloat() - 0.5, random.nextFloat() - 0.5 , random.nextFloat() - 0.5);
//        Vec3d beam = end.subtract(start);
//        world.addParticle(ParticleTypes.ENCHANTED_HIT, start.x, start.y, start.z, beam.x, beam.y, beam.z);
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(BEAM_LAYER);
        Vec3d beam = (end.subtract(start));
        float x = (maxTime - time + 2 - tickDelta) / (float) maxTime;
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
//                startPos.add(norm.multiply(beam.length() * (1 - x))), endPos, 123, 171, 254,
                start, end, 123, 171, 254,
                maxTime > 0 ? (int) (255f * x) : 255, scale);
        matrices.pop();

    }

    public void remove()
    {
        this.alive = false;
    }

}
