package com.neep.meatweapons.client.renderer.meatgun;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.meatweapons.client.MWExtraModels;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.HalberdModule;
import com.neep.meatweapons.meatgun.module.ShockStaffModule;
import com.neep.neepmeat.api.processing.random_ores.RandomOres;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ShockStaffModuleRenderer implements MeatgunModuleRenderer<ShockStaffModule>
{
    private final ItemRenderer itemRenderer;

    public ShockStaffModuleRenderer(MinecraftClient client)
    {
        this.itemRenderer = client.getItemRenderer();
    }

    @Override
    public void render(ItemStack stack, MeatgunComponent component, ShockStaffModule module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay)
    {
        BakedModel base = itemRenderer.getModels().getModelManager().getModel(MWExtraModels.SHOCK_STAFF);
        matrices.translate(8 / 16f, 0, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(AnimationTickHolder.getRenderTime() / 20));
        matrices.translate(-8 / 16f, 0, 0);
        renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, base);
    }
}
