package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.block.fluid_transport.FluidBufferBlock;
import com.neep.neepmeat.blockentity.fluid.FluidBufferBlockEntity;
import com.neep.neepmeat.blockentity.fluid.GlassTankBlockEntity;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class FluidBufferRenderer implements BlockEntityRenderer<FluidBufferBlockEntity>
{
    public FluidBufferRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(FluidBufferBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        Direction facing = blockEntity.getCachedState().get(FluidBufferBlock.FACING);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(facing.getAxis() == Direction.Axis.Z ? 90 : 0));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(facing.getAxis() == Direction.Axis.Z ? 90 : 0));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(facing.getAxis() == Direction.Axis.X ? 90 : 0));
        matrices.translate(-0.5, -0.5, -0.5);

        WritableFluidBuffer buffer = blockEntity.getBuffer(null);
        float scale = ((float) buffer.getAmount()) / ((float) buffer.getCapacity());
        FluidVariant fluid = blockEntity.getBuffer(null).getResource();
        renderFluidCuboid(vertexConsumers, matrices, fluid, 0.1f, 0.19f, 0.93f, scale);

        matrices.pop();
    }

    public static void renderFluidCuboid(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float startXYZ, float endXZ, float endY, float scaleY)
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
        float dist = startY + (endY - startY) * scaleY;
        if (FluidVariantAttributes.isLighterThanAir(fluid))
        {
            matrices.translate(1, 1, 0);
            matrices.scale(-1, -1, 1);
        }

        float depth = 0.3f;

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

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
        }

//        emitter.square(Direction.NORTH, 0.3f, 0.1f, 1 - 0.3f, dist, depth);
//        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//        emitter.spriteColor(0, -1, -1, -1, -1);
//        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
//
//        emitter.square(Direction.SOUTH, 0.3f, 0.1f, 1 - 0.3f, dist, depth);
//        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//        emitter.spriteColor(0, -1, -1, -1, -1);
//        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
//
//        emitter.square(Direction.EAST, 0.3f, 0.1f, 1 - 0.3f, dist, depth);
//        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//        emitter.spriteColor(0, -1, -1, -1, -1);
//        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
//
//        emitter.square(Direction.WEST, 0.3f, 0.1f, 1 - 0.3f, dist, 0.3f);
//        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//        emitter.spriteColor(0, -1, -1, -1, -1);
//        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
//
//        emitter.square(Direction.UP, 0.3f, 0.3f, 0.3f, 0.3f, 1 - dist);
//        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//        emitter.spriteColor(0, -1, -1, -1, -1);
//        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
    }
}