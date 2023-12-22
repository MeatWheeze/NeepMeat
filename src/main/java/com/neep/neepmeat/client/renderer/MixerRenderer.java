package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.machine.mixer.MixerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MixerRenderer implements BlockEntityRenderer<MixerBlockEntity>
{
    public MixerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MixerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
    }
}
