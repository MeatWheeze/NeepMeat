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
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
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

        long time = be.getWorld().getTime();
//        float t = (time % 100) / 100f;

//        System.out.println(be.getItems().size());
        for (ItemInPipe offset : be.getItems())
        {
            ItemStack stack = offset.getItemStack();
//            offset.step(0.0f);
            matrices.push();

            Vec3d interp = offset.interpolate(tickDelta);
//            System.out.println(tickDelta);
            matrices.translate(interp.x, interp.y, interp.z);
            matrices.scale(0.4f, 0.4f, 0.4f);
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(0.1f));
            renderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);

            matrices.pop();
        }

        matrices.pop();
    }
}