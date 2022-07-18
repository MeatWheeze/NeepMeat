package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.blockentity.fluid.GlassTankBlockEntity;
import com.neep.neepmeat.client.model.GlassTankModel;
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
import net.minecraft.util.math.MathHelper;

public class GlassTankRenderer implements BlockEntityRenderer<GlassTankBlockEntity>
{
//    public static final SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/enchanting_table_book"));
    private static ItemStack stack = new ItemStack(Items.JUKEBOX, 1);
    Model model;

    public GlassTankRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(GlassTankBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        WritableFluidBuffer buffer = blockEntity.getBuffer(null);
        float scale = ((float) buffer.getAmount()) / ((float) buffer.getCapacity());
        buffer.renderLevel = MathHelper.lerp(0.1f, buffer.renderLevel,(buffer.getAmount()) / (float) buffer.getCapacity());
        FluidVariant fluid = blockEntity.getBuffer(null).getResource();
        renderFluidCuboid(vertexConsumers, matrices, fluid, 0.07f, 0.93f, 0.93f, buffer.renderLevel);

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
                emitter.square(direction, endXZ, endXZ, startXYZ, startXYZ, direction == Direction.UP ? 1 - dist : startY);
            }
            else
            {
                emitter.square(direction, endXZ, startXYZ, startXYZ, dist, endXZ);
            }
//            emitter.square(direction, 0.1f, 0.1f, 0.9f, 0.9f - fill, 0.9f);

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);

            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
        }
    }

    protected void rotateBlock(Direction.Axis axis, MatrixStack stack)
    {
//        switch (facing)
//        {
//            case SOUTH ->
//                    {
//                        stack.translate(0, 1.5, 0.5);
//                        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
//                    }
//            case WEST ->
//                    {
//                        stack.translate(-0.5, 1.5, 0);
//                        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
//                    }
//            case NORTH ->
//                    {
//                        stack.translate(0, 1.5, -0.5);
//                        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
//                    }
//            case EAST ->
//                    {
//                        stack.translate(0.5, 1.5, 0);
//                        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90));
//                    }
//            case UP ->
//                    {
//                        stack.translate(0, 2, 0);
//                        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0));
//                    }
//            case DOWN ->
//                    {
//                        stack.translate(0, 1, 0);
//                        stack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(180));
//                    }
//        }
    }

    public Identifier getTexture(GlassTankBlockEntity entity)
    {
        return new Identifier("textures/block/bone_block_side.png");
    }
}