package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.entity.TankMinecartEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;


@Environment(value = EnvType.CLIENT)
public class TankMinecartRenderer extends MinecartEntityRenderer<TankMinecartEntity>
{
    public TankMinecartRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer)
    {
        super(ctx, layer);
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
        if (FluidVariantAttributes.isLighterThanAir(fluid))
        {
            matrices.translate(1, 1, 0);
            matrices.scale(-1, -1, 1);
        }

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

    @Override
    public void render(TankMinecartEntity minecart, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        super.render(minecart, f, g, matrices, vertexConsumerProvider, i);
    }

    @Override
    protected void renderBlock(TankMinecartEntity entity, float delta, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        super.renderBlock(entity, delta, state, matrices, vertexConsumers, light);
        SingleVariantStorage<FluidVariant> storage = entity.getBuffer(null);
        FluidVariant fluid = storage.getResource();
        float scale = storage.getAmount() / (float) storage.getCapacity();
        renderFluidCuboid(vertexConsumers, matrices, fluid, 0.1f, 0.1f, 0.9f, 0.9f, scale);
    }

}
