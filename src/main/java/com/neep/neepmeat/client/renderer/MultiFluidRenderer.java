package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.transfer.MultiFluidBuffer;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MultiFluidRenderer
{
    public static void renderMultiFluid(MultiFluidBuffer multi, float radius, float maxHeight, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        float start = 0;
        matrices.translate(0.05, 0, 0.05);
        matrices.scale(2 * radius - 0.1f, maxHeight, 2 * radius - 0.1f);
        int max = 0;
        for (Map.Entry<FluidVariant, Long> slot : multi.getSlots())
        {
            float height = slot.getValue() / (float) multi.getCapacity();
            renderFluidCuboid(vertexConsumers, matrices,
                    slot.getKey(),
                    start,
                    start + height,
                    start + height,
                    1, light);
            start += height;
            ++max;
        }
//        System.out.println(max);
    }

    public static void renderFluidCuboid(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float startXYZ, float endXZ, float endY, float scaleY, int light)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayers.getEntityBlockLayer(Blocks.BLACK_STAINED_GLASS.getDefaultState(), false));
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;

        if (fluid.isBlank() || scaleY == 0)
        {
            return;
        }

        float startY = startXYZ;
        float dist = startY + (endY - startY) * scaleY;

        float depth = 1f;

        assert renderer != null;
        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        for (Direction direction : Direction.values())
        {
            if (direction == Direction.UP)
            {
                emitter.square(Direction.UP, depth, depth, 1 - depth, 1 - depth, 1 - dist);
            }
            else if (direction != Direction.DOWN)
            {
                emitter.square(direction, depth, startY, 1 - depth, dist, depth);
            }

            if (sprite != null)
            {
                emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
                emitter.spriteColor(0, -1, -1, -1, -1);
            }

            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, light, OverlayTexture.DEFAULT_UV);
        }
    }
}
