package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.item.AnimatedSword;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@Environment(value = EnvType.CLIENT)
public class SwordRenderer<T extends AnimatedSword> extends GeoItemRenderer<T>
{
    public SwordRenderer(GeoModel<T> modelProvider)
    {
        super(modelProvider);
    }

    public void render(T animatable, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn,
                       ItemStack itemStack, ModelTransformationMode mode)
    {
//        this.currentItemStack = itemStack;
//        AnimationEvent<AnimatedSword> itemEvent = new AnimationEvent<>(animatable, 0, 0,
//                MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
//
//        stack.push();
//        stack.translate(0.5, 0.5, 0.5);
//
//        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(animatable));
//        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(animatable));
//        Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
//        RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
//                getTextureLocation(animatable));
//        render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
//                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
//                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//        stack.pop();

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
