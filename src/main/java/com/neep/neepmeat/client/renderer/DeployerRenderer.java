package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.DeployerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class DeployerRenderer implements BlockEntityRenderer<DeployerBlockEntity>
{
    public DeployerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(DeployerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        ItemStack stack = be.getResource().toStack((int) be.getAmount());

//        be.stackRenderDelta = MathHelper.lerp(delta, be.stackRenderDelta, be.getAmount() <= 0 ? 0.3f : 0f);
//        matrices.translate(0.5, 0.25f + be.stackRenderDelta, 0.5);
        matrices.translate(0.5, 0.5, 0.5);
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

        matrices.pop();
    }
}