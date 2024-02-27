package com.neep.meatweapons.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class BeamEffect extends BeamGraphicsEffect
{
    public BeamEffect(World world, UUID uuid, PacketByteBuf buf)
    {
        super(world, uuid, buf);
//        super(world, start, end, velocity, scale, maxTime);
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
        VertexConsumer consumer = consumers.getBuffer(Client.BEAM_FUNC.apply(Client.BEAM_TEXTURE));
        Vec3d beam = (end.subtract(start));
        float x = Math.max(0, maxTime - time - tickDelta) / (float) maxTime;
//        float x = 1;
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
//                startPos.add(norm.multiply(beam.length() * (1 - x))), endPos, 123, 171, 254,
                start, end, 123, 171, 254,
                maxTime > 0 ? (int) (255f * x) : 255, scale, 255);
        matrices.pop();
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void init()
        {

        }


        // All these things come from RenderPhase. They may appear public, but that is due to Architectury accessWideners.
        public static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () ->
        {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });

        public static final RenderPhase.Cull DISABLE_CULLING = new RenderPhase.Cull(false);

        public static final RenderPhase.WriteMaskState COLOR_MASK = new RenderPhase.WriteMaskState(true, false);

        public static final Function<Identifier, RenderLayer> BEAM_FUNC = Util.memoize((texture) ->
        {
            RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                    .program(new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeBeaconBeamProgram))
                    .texture(new RenderPhase.Texture(texture, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .cull(DISABLE_CULLING)
                    .writeMaskState(COLOR_MASK)
                    .build(false);
            return RenderLayer.of("beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
        });

        public static final Identifier BEAM_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/beam.png");

    }
}
