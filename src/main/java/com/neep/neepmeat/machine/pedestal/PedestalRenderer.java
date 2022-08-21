package com.neep.neepmeat.machine.pedestal;

import com.neep.neepmeat.storage.WritableStackStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity>
{
    public PedestalRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(PedestalBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        WritableStackStorage storage = be.storage;
        ItemStack stack = storage.getResource().toStack((int) storage.getAmount());

        matrices.translate(0.5, 0.67f , 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((be.getWorld().getTime() + tickDelta) * 1));
//
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

        matrices.pop();
    }
}
