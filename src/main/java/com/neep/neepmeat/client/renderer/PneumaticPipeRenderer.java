package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.util.PipeOffset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class PneumaticPipeRenderer<T extends PneumaticPipeBlockEntity> implements BlockEntityRenderer<T>
{

    public PneumaticPipeRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        long time = be.getWorld().getTime();
        float t = (time % 50) / 50f;

        for (Pair<PipeOffset, ItemStack> pair : be.getItems())
        {
            ItemStack stack = pair.getRight();
            PipeOffset offset = pair.getLeft();
            offset.step(t);
            matrices.push();

            matrices.translate(offset.x, offset.y, offset.z);
            matrices.scale(0.4f, 0.4f, 0.4f);
            renderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);

            matrices.pop();
        }

        matrices.pop();
    }
}