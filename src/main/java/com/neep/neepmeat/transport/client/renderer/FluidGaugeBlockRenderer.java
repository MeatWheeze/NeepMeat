package com.neep.neepmeat.transport.client.renderer;

import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidGaugeBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class FluidGaugeBlockRenderer implements BlockEntityRenderer<FluidGaugeBlockEntity>
{
    public FluidGaugeBlockRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(FluidGaugeBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        matrices.push();
//
//        BERenderUtils.rotateFacing(be.getCachedState().get(FluidGaugeBlock.FACING), matrices);
//
//
//        be.getLastFluid();
//        Sprite sprite = FluidVariantRendering.getSprite(fluid);
//        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
//        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
//
//        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
//        QuadEmitter emitter = renderer.meshBuilder().getEmitter();
//
//        emitter.
//
//        matrices.pop();
    }
}
