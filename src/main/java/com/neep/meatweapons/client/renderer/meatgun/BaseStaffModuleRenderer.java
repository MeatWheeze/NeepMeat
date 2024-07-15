package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.BaseStaffModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class BaseStaffModuleRenderer implements MeatgunModuleRenderer<BaseStaffModule>
{
    private final ItemRenderer itemRenderer;

    public BaseStaffModuleRenderer(MinecraftClient client)
    {
        this.itemRenderer = client.getItemRenderer();
    }

    @Override
    public void render(ItemStack stack, MeatgunComponent component, BaseStaffModule module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0, -1, 0);
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.STAFF_BASE);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
        matrices.pop();
    }
}
