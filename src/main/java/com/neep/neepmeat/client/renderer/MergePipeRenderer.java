package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.transport.block.item_transport.entity.MergePipeBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MergePipeRenderer<T extends MergePipeBlockEntity> extends ItemPipeRenderer<T>
{

    public MergePipeRenderer(BlockEntityRendererFactory.Context context)
    {
        super(context);
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        super.render(be, tickDelta, matrices, vertexConsumers, light, overlay);
    }
}