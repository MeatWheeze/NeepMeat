package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.util.ItemInPipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

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

        for (ItemInPipe item : be.getItems())
        {
            ItemStack stack = item.getItemStack();
            matrices.push();

            long diff = be.getWorld().getTime() - item.tickStart;
            float progress = (diff + tickDelta) * item.speed;
            item.set(item.getPosition(progress));

            matrices.translate(item.x, item.y, item.z);
            matrices.scale(0.4f, 0.4f, 0.4f);
//            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(0.1f));
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float) (Math.PI / 2)));
            renderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);

            matrices.pop();
        }

        matrices.pop();
    }
}