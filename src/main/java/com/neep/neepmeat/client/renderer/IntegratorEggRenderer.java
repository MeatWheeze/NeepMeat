package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.integrator.IntegratorEggBlockEntity;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class IntegratorEggRenderer implements BlockEntityRenderer<IntegratorEggBlockEntity>
{
    public IntegratorEggRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(IntegratorEggBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        matrices.push();
        if (blockEntity.canGrow())
        {
            float eggScale = 1 + (float) Math.sin(blockEntity.getWorld().getTime() / 50f) / 16;
            matrices.translate(0.5, 0, 0.5);
            matrices.scale(eggScale, eggScale, eggScale);
            matrices.translate(-0.5, 0, -0.5);
        }

        BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
        manager.getModelRenderer().render(
                blockEntity.getWorld(),
                manager.getModel(blockEntity.getCachedState()),
                blockEntity.getCachedState(),
                blockEntity.getPos(),
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                true,
                new Random(1),
                0,
                overlay
        );
        matrices.pop();

        FluidBuffer buffer = blockEntity.getBuffer(null);
        float scale = ((float) buffer.getAmount()) / ((float) buffer.getCapacity());
        FluidVariant fluid = blockEntity.getBuffer(null).getResource();
        matrices.translate(-1, 0, -1);
        matrices.scale(3, 2, 3);
        renderFluidCuboid(vertexConsumers, matrices, fluid, 0f, 0.01f, 0.99f, 0.99f, scale);

        matrices.pop();
    }

    public static void renderFluidCuboid(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float startXYZ, float startXZ, float endXZ, float endY, float scaleY)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayer.getTranslucent());
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        // Magic colourspace transformation copied from Modern Industrialisation
        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;

        if (fluid.isBlank() || scaleY == 0)
        {
            return;
        }

        float startY = startXYZ;
//        float dist = startY + (endY - startY) * scaleY;
        float dist = startY + (endY - startY) * scaleY;
        if (FluidVariantRendering.fillsFromTop(fluid))
        {
            matrices.translate(1, 1, 0);
            matrices.scale(-1, -1, 1);
        }



//        fill = yEnd * fill < xyzStart ?
        for (Direction direction : Direction.values())
        {
            QuadEmitter emitter = renderer.meshBuilder().getEmitter();

            if (direction.getAxis().isVertical())
            {
                emitter.square(direction, endXZ, endXZ, startXZ, startXZ, direction == Direction.UP ? 1 - dist : startY);
            }
            else
            {
                // Nasty bodge because I can't be bothered to fix this
                emitter.square(direction, endXZ, startXYZ, startXZ, dist, endXZ);
            }
//            emitter.square(direction, 0.1f, 0.1f, 0.9f, 0.9f - fill, 0.9f);

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_ROTATE_90);
            emitter.spriteColor(0, -1, -1, -1, -1);

            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
        }
    }
}
