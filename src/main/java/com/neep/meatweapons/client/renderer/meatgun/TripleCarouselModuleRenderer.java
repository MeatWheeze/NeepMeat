package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.TripleCarouselModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class TripleCarouselModuleRenderer implements MeatgunModuleRenderer<TripleCarouselModule>
{
    private final ItemRenderer itemRenderer;
    private final MinecraftClient client;

    public TripleCarouselModuleRenderer(MinecraftClient client)
    {
        this.itemRenderer = client.getItemRenderer();
        this.client = client;
    }

    @Override
    public void render(ItemStack stack, MeatgunComponent component, TripleCarouselModule module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay)
    {
        int selected = module.getSelected();
        float rotationThing = module.getRotation(time, tickDelta);

        float baseRotationDeg = selected * 120;
//        float baseRotationDeg = 0;
        float rotation = baseRotationDeg + rotationThing * 120;
        module.lerpAngle = MathHelper.lerpAngleDegrees(0.6f, module.lerpAngle, rotation);

        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(rotation));
        matrices.translate(-0.5, 0, -0.5);
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.TRIPLE_CAROUSEL);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
    }
}
