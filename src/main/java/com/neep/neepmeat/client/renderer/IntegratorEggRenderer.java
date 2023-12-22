package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.List;
import java.util.Random;

public class IntegratorEggRenderer extends GeoBlockRenderer<IntegratorBlockEntity>
{
    protected int playerSearch = 0;

    public IntegratorEggRenderer(BlockEntityRendererFactory.Context context)
    {
        super(new IntegratorEggModel<IntegratorBlockEntity>());
    }

    @Override
    public void render(IntegratorBlockEntity blockEntity, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLightIn)
    {
        if (!blockEntity.isMature)
        {
            renderEgg(matrices, blockEntity, vertexConsumers);
        }
        else
        {
            if (playerSearch == 0)
            {
                playerSearch = 15;
                BlockPos pos = blockEntity.getPos();
                Box box = new Box(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
                List<Entity> players = blockEntity.getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), box, (e) -> true);
                if (players.size() > 0)
                {
                    Vec2f vec = NMMaths.flatten(players.get(0).getPos().subtract(Vec3d.ofCenter(blockEntity.getPos())));
                    blockEntity.targetFacing = NMMaths.getAngle(vec);
                }
            }
            else
            {
                --playerSearch;
            }

            blockEntity.facing = NMMaths.angleLerp(0.03f, blockEntity.facing, blockEntity.targetFacing);

            renderBase(matrices, blockEntity, vertexConsumers);
            matrices.push();
            matrices.translate(0.5d, 0d, 0.5d);
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(blockEntity.facing));
            matrices.translate(-0.5d, 0d, -0.5d);
            matrices.translate(0, 1.8 + Math.sin((blockEntity.getWorld().getTime() + partialTicks) / 20) / 15, 0);
            super.render(blockEntity, partialTicks, matrices, vertexConsumers, packedLightIn);
            matrices.pop();
        }
    }

    public static void renderBase(MatrixStack matrices, IntegratorBlockEntity be, VertexConsumerProvider vertexConsumers)
    {
        BakedModelManager manager = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelManager();
        BakedModel handle = BakedModelManagerHelper.getModel(manager, NMExtraModels.INTEGRATOR_BASE);
        BlockModelRenderer renderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        renderer.render(
                be.getWorld(),
                handle,
                be.getCachedState(),
                be.getPos(),
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                true,
                new Random(0),
                0,
                0
        );
    }

    public static void renderEgg(MatrixStack matrices, IntegratorBlockEntity blockEntity, VertexConsumerProvider vertexConsumers)
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
        int overlay = 0;
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

//        WritableFluidBuffer buffer = blockEntity.getInputBuffer();
//        float scale = ((float) buffer.getAmount()) / ((float) buffer.getCapacity());
//        FluidVariant fluid = buffer.getResource();

        matrices.translate(-1, 0, -1);
        matrices.scale(3, 2, 3);
//        IntegratorEggRenderer.renderFluidCuboid(vertexConsumers, matrices, fluid, 0f, 0.01f, 0.99f, 0.99f, scale);

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
        if (FluidVariantAttributes.isLighterThanAir(fluid))
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
