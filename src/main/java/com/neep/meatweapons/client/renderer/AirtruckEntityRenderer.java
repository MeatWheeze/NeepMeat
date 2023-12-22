package com.neep.meatweapons.client.renderer;

import com.google.common.collect.Lists;
import com.neep.meatweapons.client.model.AirtruckModel;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import com.neep.meatweapons.entity.AirtruckEntity;
import com.neep.meatweapons.entity.CannonBulletEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class AirtruckEntityRenderer<T extends AbstractVehicleEntity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T>
{
    public EntityModel<CannonBulletEntity> model;

    protected GeoModelProvider<T> modelProvider;
    protected final List<GeoLayerRenderer<T>> layerRenderers = Lists.newArrayList();
    private Identifier whTexture;
    private VertexConsumerProvider rtb;

    public AirtruckEntityRenderer(EntityRendererFactory.Context context)
    {
        super(context);
        this.modelProvider = (GeoModelProvider<T>) new AirtruckModel();
    }

    @Override
    public Identifier getTexture(T entity)
    {
        return getTextureLocation(entity);
    }

    @Override
    public GeoModelProvider<T> getGeoModelProvider()
    {
        return modelProvider;
    }

    @Override
    public Identifier getTextureLocation(T instance)
    {
        return this.modelProvider.getTextureLocation(instance);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack,
                       VertexConsumerProvider bufferIn, int packedLightIn) {
        stack.push();
        EntityModelData entityModelData = new EntityModelData();

        float f = MathHelper.lerpAngleDegrees(partialTicks, entity.prevYaw, entity.yaw);

        float f7 = this.handleRotationFloat(entity, partialTicks);
        this.applyRotations(entity, stack, f7, f, partialTicks);

        float lastLimbDistance = 0.0F;
        float limbSwing = 0.0F;

        AnimationEvent<T> predicate = new AnimationEvent<T>(entity, limbSwing, lastLimbDistance, partialTicks,
                !(lastLimbDistance > -0.15F && lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
        if (modelProvider instanceof IAnimatableModel)
        {
            ((IAnimatableModel<T>) modelProvider).setLivingAnimations(entity, this.getUniqueID(entity), predicate);
        }

        stack.translate(0, 0.01f, 0);
        MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entity));
        Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn,
                getTexture(entity));
        boolean invis = entity.isInvisibleTo(MinecraftClient.getInstance().player);

        render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn,
                getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, invis ? 0.0F : (float) renderColor.getAlpha() / 255);

//        if (!entity.isSpectator()) {
//            for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
//                layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, lastLimbDistance, partialTicks,
//                        f7, netHeadYaw, headPitch);
//            }
//        }

        if (FabricLoader.getInstance().isModLoaded("patchouli"))
        {
            PatchouliCompat.patchouliLoaded(stack);
        }
        stack.pop();
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    @Override
    public Integer getUniqueID(T animatable)
    {
        return animatable.getUuid().hashCode();
    }

    @Override
    public void renderEarly(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
                            VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
                            float partialTicks)
    {
        this.rtb = renderTypeBuffer;
        this.whTexture = this.getTextureLocation(animatable);
        IGeoRenderer.super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn,
                packedOverlayIn, red, green, blue, partialTicks);
    }

    protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw,
                                  float partialTicks)
    {
        EntityPose pose = entityLiving.getPose();
        if (pose != EntityPose.SLEEPING)
        {
            matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotationYaw));
        }
    }

    public static int getPackedOverlay(Entity livingEntityIn, float uIn)
    {
        return OverlayTexture.getUv(OverlayTexture.getU(uIn), false);
    }

    protected float handleRotationFloat(T livingBase, float partialTicks)
    {
        return (float) livingBase.age + partialTicks;
    }

    public final boolean addLayer(GeoLayerRenderer<T> layer)
    {
        return this.layerRenderers.add(layer);
    }
}
