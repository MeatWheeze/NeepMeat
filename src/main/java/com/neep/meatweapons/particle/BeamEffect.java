package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.BEACON_BEAM_SHADER;
import static net.minecraft.client.render.RenderPhase.ENTITY_TRANSLUCENT_SHADER;

public class BeamEffect extends GraphicsEffect
{
    public static final Function<Identifier, RenderLayer> BEAM_FUNC = Util.memoize((texture) ->
    {
        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                .shader(BEACON_BEAM_SHADER)
                .texture(new RenderPhase.Texture(texture, false, false))
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .cull(RenderPhase.DISABLE_CULLING)
                .writeMaskState(RenderPhase.COLOR_MASK)
                .build(false);
        return RenderLayer.of("beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
    });

    public static final Identifier BEAM_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/beam.png");
    public static final RenderLayer BEAM_LAYER = BEAM_FUNC.apply(BEAM_TEXTURE);

    public BeamEffect(World world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        super(world, start, end, velocity, scale, maxTime);
    }

    @Environment(value= EnvType.CLIENT)
    public void tick()
    {
        super.tick();

        Random random = new Random(world.getTime());
        double d = 0.1;
        random.nextFloat();
//        world.addParticle(ParticleTypes.ENCHANTED_HIT, end.x, end.y, end.z, random.nextFloat() - 0.5, random.nextFloat() - 0.5 , random.nextFloat() - 0.5);
        world.addParticle(MWParticles.PLASMA_PARTICLE, end.x, end.y, end.z,
                d * (random.nextFloat() - 0.75),
//                0,
                d * (random.nextFloat() - 0.5),
                d * (random.nextFloat() - 0.5));
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(BEAM_LAYER);
        Vec3d beam = (end.subtract(start));
        float x = Math.max(0, maxTime - time - tickDelta) / (float) maxTime;
//        float x = 1;
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
//                startPos.add(norm.multiply(beam.length() * (1 - x))), endPos, 123, 171, 254,
                start, end, 123, 171, 254,
                maxTime > 0 ? (int) (255f * x) : 255, scale);
        matrices.pop();
    }
}
