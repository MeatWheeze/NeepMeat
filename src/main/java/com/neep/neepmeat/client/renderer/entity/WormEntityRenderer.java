package com.neep.neepmeat.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.entity.WormEntityModel;
import com.neep.neepmeat.entity.worm.WormEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

public class WormEntityRenderer extends GeoEntityRenderer<WormEntity>
{
    protected static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "block/duat_stone.png");

    public WormEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx, new WormEntityModel());
    }

    @Override
    public void render(WormEntity entity, float entityYaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int packedLight)
    {
        setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
//        matrices.push();
//
//        this.dispatchedMat = matrices.peek().getPositionMatrix().copy();
//
//        float lerpHeadRot = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw);
//        float headPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
//
//        float ageInTicks = entity.age + tickDelta;
//        float limbSwingAmount = 0;
//        float limbSwing = 0;
//
//        applyRotations(entity, matrices, headPitch, lerpHeadRot, tickDelta);
//
//        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(entity));
//
//        this.modelProvider.setCustomAnimations(entity, getInstanceId(entity));
//
//        matrices.translate(0, 0.01f, 0);
//        RenderSystem.setShaderTexture(0, getTextureLocation(entity));
//
//        Color renderColor = getRenderColor(entity, tickDelta, matrices, vcp, null, packedLight);
//        RenderLayer renderType = getRenderType(entity, tickDelta, matrices, vcp, null, packedLight,
//                getTextureLocation(entity));
//
//        VertexConsumer glintBuffer = vcp.getBuffer(RenderLayer.getDirectEntityGlint());
//        VertexConsumer translucentBuffer = vcp
//                .getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(entity)));
//
//        render(model, entity, tickDelta, renderType, matrices, vcp,
//                glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer)
//                        : null,
//                packedLight, getOverlay(entity, 0), renderColor.getRed() / 255f,
//                renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
//                renderColor.getAlpha() / 255f);
//
//        for (GeoLayerRenderer<WormEntity> layerRenderer : this.layerRenderers)
//        {
//            renderLayer(matrices, vcp, packedLight, entity, limbSwing, limbSwingAmount, tickDelta, ageInTicks,
//                    0, headPitch, vcp, layerRenderer);
//        }
//
//        matrices.pop();

        renderSegment(entity, entity, matrices, tickDelta, vcp, packedLight);

        for (WormEntity.WormSegment segment : entity.getParts())
        {
            matrices.push();
            matrices.translate(
                    MathHelper.lerp(tickDelta, segment.prevX - entity.getX(), segment.getX() - entity.getX()),
                    MathHelper.lerp(tickDelta, segment.prevY - entity.getY(), segment.getY() - entity.getY()),
                    MathHelper.lerp(tickDelta, segment.prevZ - entity.getZ(), segment.getZ() - entity.getZ()));
            renderSegment(entity, segment, matrices, tickDelta, vcp, packedLight);
            matrices.pop();

            segment.lastRenderX = segment.getX();
            segment.lastRenderY = segment.getY();
            segment.lastRenderZ = segment.getZ();
        }
    }

    protected void renderSegment(WormEntity base, Entity segment, MatrixStack matrices, float tickDelta, VertexConsumerProvider vcp, int packedLight)
    {
        matrices.push();

        this.dispatchedMat = matrices.peek().getPositionMatrix().copy();

//        float lerpYaw = MathHelper.lerpAngleDegrees(tickDelta, segment.prevYaw, segment.getYaw());
//        float lerpPitch = MathHelper.lerp(tickDelta, segment.prevPitch, segment.getPitch());
        float lerpYaw = segment.getYaw();
        float lerpPitch = segment.getPitch();

        float ageInTicks = base.age + tickDelta;

        applyRotations(segment, matrices, lerpPitch, lerpYaw, tickDelta);

        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(base));

        this.modelProvider.setCustomAnimations(base, getInstanceId(base));

        matrices.translate(0, 0.01f, 0);
        RenderSystem.setShaderTexture(0, getTextureLocation(base));

        Color renderColor = getRenderColor(base, tickDelta, matrices, vcp, null, packedLight);
        RenderLayer renderType = getRenderType(base, tickDelta, matrices, vcp, null, packedLight,
                getTextureLocation(base));

        VertexConsumer glintBuffer = vcp.getBuffer(RenderLayer.getDirectEntityGlint());
        VertexConsumer translucentBuffer = vcp
                .getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(base)));

        render(model, base, tickDelta, renderType, matrices, vcp,
                glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer)
                        : null,
                packedLight, getOverlay(base, 0), renderColor.getRed() / 255f,
                renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
                renderColor.getAlpha() / 255f);

        for (GeoLayerRenderer<WormEntity> layerRenderer : this.layerRenderers)
        {
            renderLayer(matrices, vcp, packedLight, base, 0, 0, tickDelta, ageInTicks,
                    0, lerpPitch, vcp, layerRenderer);
        }

        matrices.pop();

    }

    @Override
    public boolean shouldRender(WormEntity entity, Frustum frustum, double x, double y, double z)
    {
        return super.shouldRender(entity, frustum, x, y, z);
    }

    protected void applyRotations(Entity entity, MatrixStack matrices, float pitch, float yaw, float tickDelta)
    {
        matrices.translate(0, 0.5, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - yaw));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f - pitch));
        matrices.translate(0, -0.5, 0);
    }

    @Override
    public Identifier getTexture(WormEntity entity)
    {
        return TEXTURE;
    }
}
