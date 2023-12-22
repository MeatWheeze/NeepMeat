package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.entity.bovine_horror.BovineHorrorEntity;
import com.neep.neepmeat.util.SightUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BovineHorrorRenderer extends GeoEntityRenderer<BovineHorrorEntity>
{
    public BovineHorrorRenderer(EntityRendererFactory.Context renderManager)
    {
        super(renderManager, new GenericModel<>(NeepMeat.NAMESPACE,
                "geo/bovine_horror.geo.json",
                "textures/entity/bovine_horror.png",
                "animations/bovine_horror.animation.json"
                ));

        this.shadowRadius = 1.5F;
    }

    @Override
    public void render(BovineHorrorEntity animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight)
    {
        animatable.prevVisibility = (float) MathHelper.lerp(0.1, animatable.prevVisibility, animatable.getVisibility());
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public Color getRenderColor(BovineHorrorEntity animatable, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vcp, VertexConsumer vertexConsumer, int packedLight)
    {
        float alpha = animatable.prevVisibility;
        if (SightUtil.canPlayerSee(MinecraftClient.getInstance().player, animatable))
        {
            alpha = 1;
        }
        return Color.ofRGBA(1, 1, 1, alpha);
    }

    @Override
    public RenderLayer getRenderType(BovineHorrorEntity animatable, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, Identifier texture)
    {
        if (SightUtil.canPlayerSee(MinecraftClient.getInstance().player, animatable))
        {
            return RenderLayer.getEntityCutout(texture);
        }
        return RenderLayer.getEntityTranslucent(texture);
    }

    @Override
    protected void applyRotations(BovineHorrorEntity animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick)
    {
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - rotationYaw));
    }
}
