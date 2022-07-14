package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.client.renderer.MultiFluidRenderer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("UnstableApiUsage")
public class MixerRenderer implements BlockEntityRenderer<MixerBlockEntity>
{
    public MixerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MixerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (be.getCurrentRecipe() != null)
        {
            FluidVariant var1 = (FluidVariant) be.getCurrentRecipe().fluidInput1.resource();
            FluidVariant var2 = (FluidVariant) be.getCurrentRecipe().fluidInput2.resource();
            FluidVariant var3 = (FluidVariant) be.getCurrentRecipe().fluidOutput.resource();

            float progress = (be.getWorld().getTime() + tickDelta - be.processStart) / (float) be.processTime;
            float scale = (-Math.abs(2 * progress - 1) + 1) * 0.25f;

            matrices.push();
            matrices.scale(0.9f, 1, 0.9f);
            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var1, 0, scale, scale, 1);
            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var2, 0 + scale, scale * 2, scale * 2, 1);

            if (progress > 0.5f)
            {
                float offset = 0.5f - scale;
                MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var3, scale * 2, 0.5f, 0.5f, 1);
            }
            matrices.pop();
        }
    }
}
