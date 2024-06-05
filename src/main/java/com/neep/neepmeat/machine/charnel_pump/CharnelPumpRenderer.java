package com.neep.neepmeat.machine.charnel_pump;

import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class CharnelPumpRenderer implements BlockEntityRenderer<CharnelPumpBlockEntity>
{
    public CharnelPumpRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    public static float plungerAnimation(float t)
    {
        float s;
        if (t < 70)
        {
            s = easeInOutSine(t / 70);
        }
        else
        {
            s = 1 - (t - 70) / 30;
        }
        return s;
    }

    public static float easeInOutSine(float x)
    {
        return (float) (-(Math.cos(Math.PI * x) - 1) / 2);
    }

    @Override
    public void render(CharnelPumpBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay)
    {
        int upLight = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().up(1));

//        float s = (NMMaths.sin(be.getWorld().getTime(), AnimationTickHolder.getPartialTicks(), 0.1f) + 1) / 2;
        float t = be.animationTicks > 0 ? 100 - be.animationTicks + tickDelta : 0;
        float height = 3 * plungerAnimation(t) + 4 / 16f;

        FluidVariant fluidVariant = FluidVariant.of(NMFluids.STILL_WORK_FLUID);
        Sprite sprite = FluidVariantRendering.getSprite(fluidVariant);
        if (sprite == null)
            return;

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        assert renderer != null;
        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        VertexConsumer consumer = vcp.getBuffer(RenderLayers.getEntityBlockLayer(Blocks.BLACK_STAINED_GLASS.getDefaultState(), false));

        matrices.translate(0, 3, 0);
        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
        renderFluidColumn(height, 0.9f, matrices, sprite, emitter, consumer, upLight);
    }

    private static void renderFluidColumn(float height, float radius, MatrixStack matrices, Sprite sprite, QuadEmitter emitter, VertexConsumer consumer, int light)
    {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        float k = 0.0F;
        float m = 0.0F;
        float l = radius;
        float g = height;

        float cr = 1;
        float cg = 1;
        float cb = 1;

        for (int seg = 1; seg <= 8; ++seg)
        {
            float o = MathHelper.sin(seg * 2 * MathHelper.PI / 8.0F) * radius;
            float p = MathHelper.cos(seg * 2 * MathHelper.PI / 8.0F) * radius;
            float q = seg / 8.0F;

            emitter.pos(0, new Vector3f(k, l, 0)).uv(0, u1, v0);
            emitter.pos(1, new Vector3f(k, l, g)).uv(1, u0, v0);
            emitter.pos(2, new Vector3f(o, p, g)).uv(2, u0, v1);
            emitter.pos(3, new Vector3f(o, p, 0)).uv(3, u1, v1);
            BakedQuad quad = emitter.toBakedQuad(sprite);
            consumer.quad(matrices.peek(), quad, cr, cg, cb, light, OverlayTexture.DEFAULT_UV);

            k = o;
            l = p;
        }
    }
}
