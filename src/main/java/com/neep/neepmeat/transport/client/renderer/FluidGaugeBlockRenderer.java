package com.neep.neepmeat.transport.client.renderer;

import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.transport.block.fluid_transport.FluidGaugeBlock;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidGaugeBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
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
