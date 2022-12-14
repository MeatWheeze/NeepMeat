package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.Random;

public class IntegratorEggRenderer extends GeoBlockRenderer<IntegratorBlockEntity>
{
    private static final Identifier LAYER = new Identifier(NeepMeat.NAMESPACE, "textures/entity/integrator_basic_overlay.png");

    public IntegratorEggRenderer(BlockEntityRendererFactory.Context context)
    {
        super(new IntegratorEggModel<IntegratorBlockEntity>());
    }

    @Override
    public void render(IntegratorBlockEntity be, float partialTicks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLightIn)
    {
        if (!be.isMature)
        {
            renderEgg(matrices, be, vertexConsumers);
        }
        else
        {
            Vec2f vec = NMMaths.flatten(be.getLookTarget().subtract(Vec3d.ofCenter(be.getPos())));
            be.targetFacing = NMMaths.getAngle(vec);

            be.facing = NMMaths.angleLerp(0.03f, be.facing, be.targetFacing);

            // Rotate towards target
            renderBase(matrices, be, vertexConsumers);
            matrices.push();
            matrices.translate(0.5d, 0d, 0.5d);
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(be.facing));
            matrices.translate(-0.5d, 0d, -0.5d);
            matrices.translate(0, 1.8 + Math.sin((be.getWorld().getTime() + partialTicks) / 20) / 15, 0);

            AnimatedGeoModel<IntegratorBlockEntity> modelProvider = getGeoModelProvider();
            GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(be));
            modelProvider.setCustomAnimations(be, this.getInstanceId(be));
            matrices.push();
            matrices.translate(0, 0.01f, 0);
            matrices.translate(0.5, 0, 0.5);

            // Render main model
            MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(be));
            Color renderColor = getRenderColor(be, partialTicks, matrices, vertexConsumers, null, packedLightIn);
            RenderLayer renderType = getRenderType(be, partialTicks, matrices, vertexConsumers, null, packedLightIn, getTextureLocation(be));
            render(model, be, partialTicks, renderType, matrices, vertexConsumers, null, packedLightIn, OverlayTexture.DEFAULT_UV,
                    (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                    (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

            // Render enlightenment overlay
            RenderLayer cameo =  RenderLayer.getEntityTranslucent(LAYER);
            render(model, be, partialTicks, cameo, matrices, vertexConsumers,
                    vertexConsumers.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, be.getData() / IntegratorBlockEntity.MAX_DATA);

            matrices.pop();
            matrices.pop();
        }
    }

    public static void renderBase(MatrixStack matrices, IntegratorBlockEntity be, VertexConsumerProvider vertexConsumers)
    {
        BERenderUtils.renderModel(NMExtraModels.INTEGRATOR_BASE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
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

        matrices.translate(-1, 0, -1);
        matrices.scale(3, 2, 3);
        matrices.pop();
    }
}
