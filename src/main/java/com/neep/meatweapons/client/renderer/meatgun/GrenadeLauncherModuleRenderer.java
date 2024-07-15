package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.meatgun.module.GrenadeLauncherModule;
import com.neep.meatweapons.component.MeatgunComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class GrenadeLauncherModuleRenderer implements MeatgunModuleRenderer<GrenadeLauncherModule>
{
    private final ItemRenderer itemRenderer;

    public GrenadeLauncherModuleRenderer(MinecraftClient client)
    {
        this.itemRenderer = client.getItemRenderer();
    }

    @Override
    public void render(ItemStack stack, MeatgunComponent component, GrenadeLauncherModule module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay)
    {
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.GRENADE_LAUNCHER);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
    }
}
