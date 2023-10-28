package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.item.AnimatedSword;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.Collections;

@Environment(value = EnvType.CLIENT)
public class SwordRenderer<T extends AnimatedSword> extends GeoItemRenderer<T>
{
    public SwordRenderer(AnimatedGeoModel<T> modelProvider)
    {
        super(modelProvider);
    }

    @Override
    public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStackIn,
                       VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack, mode);
//        if (mode.equals(ModelTransformation.Mode.GUI))
//        {
//            return;
//        }
//        super.render(itemStack, mode, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    public void render(T animatable, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn,
                       ItemStack itemStack, ModelTransformation.Mode mode)
    {
        this.currentItemStack = itemStack;
        AnimationEvent<AnimatedSword> itemEvent = new AnimationEvent<>(animatable, 0, 0,
                MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));

        stack.push();
        stack.translate(0.5, 0.5, 0.5);

        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(animatable));
        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(animatable));
        Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
                getTextureLocation(animatable));
        render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();

//        this.currentItemStack = itemStack;
//
//        AnimationEvent<SlasherItem> itemEvent = new AnimationEvent<>(animatable, 0, 0,
//                MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
//
//        if (mode.equals(ModelTransformation.Mode.GUI))
//        {
////            modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
//            return;
//        }
//
//        stack.push();
//        stack.translate(0.5, 0.5, 0.5);
//        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
//        Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
//        RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
//                getTextureLocation(animatable));
//
//        render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
//                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//        stack.pop();
    }
}
