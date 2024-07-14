package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.meatgun.module.BloodthrowerModule;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class BloodthrowerModuleRenderer implements MeatgunModuleRenderer<BloodthrowerModule>
{
    private final ItemRenderer itemRenderer;

    public BloodthrowerModuleRenderer(MinecraftClient client)
    {
        this.itemRenderer = client.getItemRenderer();
    }

    @Override
    public void render(ItemStack stack, MeatgunComponent component, BloodthrowerModule module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay)
    {
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.BLOODTHROWER);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
    }
}
