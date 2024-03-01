package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.model.AirtruckModel;
import com.neep.meatweapons.entity.AirtruckEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Collections;

public class AirtruckEntityRenderer extends GeoEntityRenderer<AirtruckEntity>
{
    public AirtruckEntityRenderer(EntityRendererFactory.Context renderManager)
    {
        super(renderManager, new AirtruckModel());
    }

    //    // any of the built-in renderers because they have incompatible class parameter bounds.
//    static
//    {
//        AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity ?
//                (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity(entity) : null);
//    }
//
//    protected GeoModelProvider<T> modelProvider;
//    protected final List<GeoLayerRenderer<T>> layerRenderers = Lists.newArrayList();
//    private Identifier whTexture;
//    private VertexConsumerProvider rtb = null;
//
//    public AirtruckEntityRenderer(EntityRendererFactory.Context context)
//    {
//        super(context);
//        this.modelProvider = (GeoModelProvider<T>) new AirtruckModel();
//    }
//
//    @Override
//    public Identifier getTexture(T entity)
//    {
//        return getTextureLocation(entity);
//    }
//
//    @Override
//    public VertexConsumerProvider getCurrentRTB()
//    {
//        return rtb;
//    }
//
//    @Override
//    public GeoModelProvider<T> getGeoModelProvider()
//    {
//        return modelProvider;
//    }
//
//    @Override
//    public Identifier getTextureLocation(T instance)
//    {
//        return this.modelProvider.getTextureResource(instance);
//    }
//
//    @Override
//    public Identifier getTextureResource(T instance)
//    {
//        return getTextureLocation(instance);
//    }
//
    public static float ease(float x)
    {
//        return (float) (1 / (1 + Math.exp(-9 * (x - 0.5))));
        return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
    }

    @Override
    public RenderLayer getRenderType(AirtruckEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick)
    {
        return RenderLayer.getEntityCutout(texture);
    }

    @Override
    public void actuallyRender(MatrixStack matrices, AirtruckEntity entity, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer vcp, boolean isReRender, float tickDelta, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        matrices.push();

        float f = MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.getYaw());

        float ageInTicks = entity.age + tickDelta;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        applyRotations1(entity, matrices, ageInTicks, f, tickDelta);

        if (!isReRender)
        {
            float headPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
            float motionThreshold = getMotionAnimThreshold(entity);
            Vec3d velocity = entity.getVelocity();
            float avgVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
            AnimationState<AirtruckEntity> animationState = new AnimationState<>(entity, limbSwing, limbSwingAmount, tickDelta, avgVelocity >= motionThreshold);
            long instanceId = getInstanceId(entity);

            animationState.setData(DataTickets.TICK, entity.getTick(entity));
            animationState.setData(DataTickets.ENTITY, entity);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(false, false, -0, -headPitch));
            this.model.addAdditionalStateData(entity, instanceId, animationState::setData);
            this.model.handleAnimations(entity, instanceId, animationState);
        }

        matrices.translate(0, 0.01f, 0);

        this.modelRenderTranslations = new Matrix4f(matrices.peek().getPositionMatrix());

        float prop = entity.forwardsVelocity / entity.maxSpeed;
        var parser = MolangParser.INSTANCE;
        parser.setValue("r_lf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
        parser.setValue("r_rf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
        parser.setValue("r_lb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
        parser.setValue("r_rb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);

        if (!entity.isInvisibleTo(MinecraftClient.getInstance().player))
            actuallyRender1(matrices, entity, model, renderType, bufferSource, vcp, isReRender, tickDelta,
                    packedLight, packedOverlay, red, green, blue, alpha);

        matrices.pop();
    }

    void actuallyRender1(MatrixStack poseStack, AirtruckEntity animatable, BakedGeoModel model, RenderLayer renderType,
                                 VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                                 int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones())
        {
            renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                    packedOverlay, red, green, blue, alpha);
        }
    }

    //    @Override
//    public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn)
//    {
//        stack.push();
//        EntityModelData entityModelData = new EntityModelData();
//
//        float f = MathHelper.lerpAngleDegrees(partialTicks, entity.prevYaw, entity.getYaw());
//
//        float f7 = this.handleRotationFloat(entity, partialTicks);
//        this.applyRotations(entity, stack, f7, f, partialTicks);
//
//        float prop = entity.forwardsVelocity / entity.maxSpeed;
//        var parser = MolangParser.INSTANCE;
//        parser.setValue("r_lf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        parser.setValue("r_rf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        parser.setValue("r_lb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        parser.setValue("r_rb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//
////        AnimationEvent<T> predicate = new AnimationEvent<T>(entity, 0, 0, partialTicks,
////                false, Collections.singletonList(entityModelData));
////        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(entity));
////        if (modelProvider instanceof IAnimatableModel)
////        {
////            ((IAnimatableModel<T>) modelProvider).setCustomAnimations(entity, this.getInstanceId(entity));
////        }
//
//        stack.translate(0, 0.01f, 0);
//        MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entity));
//        Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
//        RenderLayer renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn, getTexture(entity));
//        boolean invis = entity.isInvisibleTo(MinecraftClient.getInstance().player);
//
//        render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn,
//                getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                (float) renderColor.getBlue() / 255f, invis ? 0.0F : (float) renderColor.getAlpha() / 255);
//
//        stack.pop();
//        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
//    }
//
//    @Override
//    public int getInstanceId(T animatable)
//    {
//        return animatable.getUuid().hashCode();
//    }
//
//    @Override
//    public void renderEarly(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
//                            VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
//                            float partialTicks)
//    {
//        this.rtb = renderTypeBuffer;
//        this.whTexture = this.getTextureLocation(animatable);
//        IGeoRenderer.super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn,
//                packedOverlayIn, red, green, blue, partialTicks);
//    }
//
    protected void applyRotations1(AirtruckEntity entity, MatrixStack matrices, float ageInTicks, float rotationYaw, float tickDelta)
    {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - rotationYaw));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(entity.getRoll(tickDelta)));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(entity.getPitch(tickDelta)));
    }
//
//    public static int getPackedOverlay(Entity livingEntityIn, float uIn)
//    {
//        return OverlayTexture.getUv(OverlayTexture.getU(uIn), false);
//    }
//
//    protected float handleRotationFloat(T livingBase, float partialTicks)
//    {
//        return (float) livingBase.age + partialTicks;
//    }
//
//    public final boolean addLayer(GeoLayerRenderer<T> layer)
//    {
//        return this.layerRenderers.add(layer);
//    }
}
