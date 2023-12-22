package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.client.renderer.MultiFluidRenderer;
import com.neep.neepmeat.fluid_transfer.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public class MixerRenderer implements BlockEntityRenderer<MixerBlockEntity>
{
    public MixerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MixerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        float progress = 0;
        float nextOutput = 0;
        if (be.getCurrentRecipe() != null)
        {
            progress = (be.getWorld().getTime() + tickDelta - be.processStart) / (float) be.processTime;
            nextOutput = progress * be.getCurrentRecipe().fluidOutput.amount();
        }

        WritableSingleFluidStorage storage = (WritableSingleFluidStorage) be.storage.getFluidOutput();
        storage.renderLevel = MathHelper.lerp(0.1f, storage.renderLevel,(storage.getAmount() + nextOutput) / (float) storage.getCapacity());
        float outputEnd = storage.renderLevel;
        FluidVariant output = storage.getResource();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.9f, 1, 0.9f);
        matrices.translate(-0.5, -0.5, -0.5);
        MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, output, 0, outputEnd, outputEnd, 1);

        if (be.getCurrentRecipe() != null)
        {
            FluidVariant var1 = (FluidVariant) be.getCurrentRecipe().fluidInput1.resource();
            FluidVariant var2 = (FluidVariant) be.getCurrentRecipe().fluidInput2.resource();
            FluidVariant var3 = (FluidVariant) be.getCurrentRecipe().fluidOutput.resource();

            float scale = (-Math.abs(2 * progress - 1) + 1) * 0.1f;

            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var1, outputEnd, outputEnd + scale, outputEnd + scale, 1);
            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var2, outputEnd + scale, outputEnd + scale * 2, outputEnd + scale * 2, 1);

            if (progress > 0.5f)
            {
                float offset = 0.5f - scale;
//                MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var3, outputEnd + scale * 2, outputEnd + 0.5f, outputEnd + 0.5f, 1);
            }
        }
        matrices.pop();
    }
}
