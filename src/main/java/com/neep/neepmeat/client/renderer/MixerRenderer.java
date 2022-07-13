package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.machine.mixer.MixerTopBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MixerRenderer implements BlockEntityRenderer<MixerTopBlockEntity>
{
    public MixerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MixerTopBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
    }
}
