package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@Environment(value = EnvType.CLIENT)
public class IntegratorEggRenderer extends GeoBlockRenderer<IntegratorBlockEntity>
{
    private static final Identifier LAYER = new Identifier(NeepMeat.NAMESPACE, "textures/entity/integrator_basic_overlay.png");
    private final Random random = Random.create();

    public IntegratorEggRenderer(BlockEntityRendererFactory.Context context)
    {
        super(new IntegratorEggModel<IntegratorBlockEntity>());
    }

    @Override
    public void defaultRender(MatrixStack matrices, IntegratorBlockEntity be, VertexConsumerProvider vertexConsumers, RenderLayer renderType, VertexConsumer buffer, float yaw, float tickDelta, int packedLight)
    {
        if (!be.isMature)
        {
            renderEgg(matrices, be, vertexConsumers);
        }
        else
        {
            Vec2f vec = NMMaths.flattenY(be.getLookTarget().subtract(Vec3d.ofCenter(be.getPos())));
            be.targetFacing = NMMaths.getAngle(vec);

            be.facing = NMMaths.angleLerp(0.03f, be.facing, be.targetFacing);

            // Rotate towards target
            renderBase(matrices, be, vertexConsumers);
            matrices.push();
            matrices.translate(0.5d, 0d, 0.5d);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(be.facing));
            matrices.translate(-0.5d, 0d, -0.5d);
            matrices.translate(0, 1.8 + Math.sin((be.getWorld().getTime() + tickDelta) / 20) / 15, 0);

            GeoModel<IntegratorBlockEntity> modelProvider = getGeoModel();
//            GeoModel model = modelProvider.getModel(modelProvider.getModelResource(be));
//            modelProvider.setCustomAnimations(be, this.getInstanceId(be), );
            matrices.push();
            matrices.translate(0, 0.01f, 0);
            matrices.translate(0.5, 0, 0.5);

            // Render main model
//            MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(be));
//            Color renderColor = getRenderColor(be, tickDelta, packedLight);
//            RenderLayer renderType = RenderLayer.getEntityTranslucent(getTextureLocation(be));
//            actuallyRender(model, be, tickDelta, renderType, matrices, vertexConsumers, null, packedLight, OverlayTexture.DEFAULT_UV,
//                    (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                    (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//
//            // Render enlightenment overlay
//            float dataPropt = (float) be.getData(DataVariant.NORMAL) / IntegratorBlockEntity.MAX_DATA;
//            RenderLayer cameo =  RenderLayer.getEntityTranslucent(LAYER);
//            render(model, be, tickDelta, cameo, matrices, vertexConsumers,
//                    vertexConsumers.getBuffer(cameo), packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, dataPropt);

            matrices.pop();
            matrices.pop();
        }
    }

    public void renderBase(MatrixStack matrices, IntegratorBlockEntity be, VertexConsumerProvider vertexConsumers)
    {
        BERenderUtils.renderModelSmooth(NMExtraModels.INTEGRATOR_BASE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
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
                Random.create(),
                0,
                overlay
        );
        matrices.pop();

        matrices.translate(-1, 0, -1);
        matrices.scale(3, 2, 3);
        matrices.pop();
    }
}
