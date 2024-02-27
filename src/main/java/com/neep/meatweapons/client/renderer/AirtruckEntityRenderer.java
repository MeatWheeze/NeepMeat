package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.model.AirtruckModel;
import com.neep.meatweapons.entity.AirtruckEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AirtruckEntityRenderer<T extends AirtruckEntity> extends GeoEntityRenderer<AirtruckEntity>
{
    public AirtruckEntityRenderer(EntityRendererFactory.Context renderManager)
    {
        super(renderManager, new AirtruckModel());
    }


//    // If this class extended GeoEntityRenderer, this would be automatically executed. Unfortunately, I can't extend
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
//    public static float ease(float x)
//    {
////        return (float) (1 / (1 + Math.exp(-9 * (x - 0.5))));
//        return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
//    }
//
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
//        GeckoLibCache.getInstance().parser.setValue("r_lf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        GeckoLibCache.getInstance().parser.setValue("r_rf", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        GeckoLibCache.getInstance().parser.setValue("r_lb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//        GeckoLibCache.getInstance().parser.setValue("r_rb", () -> Math.signum(prop) * -ease(Math.abs(prop))  * 20);
//
//        AnimationEvent<T> predicate = new AnimationEvent<T>(entity, 0, 0, partialTicks,
//                false, Collections.singletonList(entityModelData));
//        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(entity));
//        if (modelProvider instanceof IAnimatableModel)
//        {
//            ((IAnimatableModel<T>) modelProvider).setCustomAnimations(entity, this.getInstanceId(entity));
//        }
//
//        stack.translate(0, 0.01f, 0);
//        MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entity));
//        Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
//        RenderLayer renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn,
//                getTexture(entity));
//        boolean invis = entity.isInvisibleTo(MinecraftClient.getInstance().player);
//
//        render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn,
//                getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                (float) renderColor.getBlue() / 255f, invis ? 0.0F : (float) renderColor.getAlpha() / 255);
//
//        if (FabricLoader.getInstance().isModLoaded("patchouli"))
//        {
//            PatchouliCompat.patchouliLoaded(stack);
//        }
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
//    protected void applyRotations(T entity, MatrixStack matrices, float ageInTicks, float rotationYaw, float tickDelta)
//    {
//        EntityPose pose = entity.getPose();
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - rotationYaw));
//        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(entity.getRoll(tickDelta)));
//        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(entity.getPitch(tickDelta)));
//    }
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
