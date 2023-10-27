package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.IAimable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class BaseGunRenderer<T extends BaseGunItem & IAnimatable> extends GeoItemRenderer<T>
{
    public Vector3f currentTransform = new Vector3f(0, 0, 0);

    public BaseGunRenderer(AnimatedGeoModel<T> model)
    {
        super(model);
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation)
    {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    public void render(ItemStack itemStack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean isAiming = player.isSneaking();
        if (mode.isFirstPerson())
        {
            Item item = itemStack.getItem();
            Vector3f transform = item instanceof IAimable aimable ? aimable.getAimOffset() : new Vector3f(0, 0, 0);
            float delta = 0.2f;
            currentTransform.lerp(isAiming ? transform : new Vector3f(0, 0, 0), delta);

            boolean stackInMain = GeckoLibUtil.getIDFromStack(player.getStackInHand(Hand.MAIN_HAND)) == GeckoLibUtil.getIDFromStack(itemStack);

            matrices.translate(
                    (stackInMain && player.getMainArm() == Arm.RIGHT) ? -currentTransform.x : currentTransform.x,
                    currentTransform.y,
                    currentTransform.z);
        }
        this.render((T) itemStack.getItem(), matrices, bufferIn, combinedLightIn, itemStack);
    }

    public void render(T animatable, MatrixStack matrices, VertexConsumerProvider bufferIn, int packedLightIn,
                       ItemStack itemStack) {

        super.render(animatable, matrices, bufferIn, packedLightIn, itemStack);

//        MinecraftClient client = MinecraftClient.getInstance();
//        this.currentItemStack = itemStack;
//        AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0,
//                MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
//        modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
//        matrices.push();
//        matrices.translate(0.5, 0.5, 0.5);
//
//        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(animatable));
//        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
//        Color renderColor = getRenderColor(animatable, 0, matrices, bufferIn, null, packedLightIn);
//        RenderLayer renderType = getRenderType(animatable, 0, matrices, bufferIn, null, packedLightIn,
//                getTextureLocation(animatable));
//        render(model, animatable, 0, renderType, matrices, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
//                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//        Optional<GeoBone> location = model.getBone("main");
////        Optional<GeoBone> location2 = model.getBone("");
//        if (location.isPresent())
//        {
//            GeoBone bone = location.get();
//            matrices.push();
//            RenderUtils.translate(bone, matrices);
//            RenderUtils.moveToPivot(bone, matrices);
//            RenderUtils.rotate(bone, matrices);
//            RenderUtils.scale(bone, matrices);
////            RenderUtils.moveBackFromPivot(bone, matrices);
////            matrices.translate(0, 1.0, 0);
//            matrices.scale(0.01f, -0.01f, 0.1f);
////            MinecraftClient.getInstance().textRenderer.draw(matrices, String.valueOf(1 - itemStack.getDamage() / itemStack.getItem().getMaxDamage()), 0.f, 0.f, 0x00bbbbbb);
//            MinecraftClient.getInstance().textRenderer.draw(matrices, "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 0.f, 0.f, 0x00bbbbbb);
//            matrices.pop();
//            matrices.pop();
//        }
    }
}
