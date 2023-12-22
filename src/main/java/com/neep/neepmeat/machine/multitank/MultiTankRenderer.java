package com.neep.neepmeat.machine.multitank;

import com.neep.meatlib.transfer.MultiFluidBuffer;
import com.neep.neepmeat.client.renderer.MultiFluidRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MultiTankRenderer implements BlockEntityRenderer<MultiTankBlockEntity>
{
    public MultiTankRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(MultiTankBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        MultiFluidBuffer buffer = be.getStorage();
        float maxHeight = 1;

        matrices.push();

        matrices.push();
        matrices.scale(1, 0.9f, 1);
        matrices.translate(0, 0.05, 0);
        MultiFluidRenderer.renderMultiFluid(buffer, 0.5f, maxHeight, matrices, vertexConsumers, light, overlay);
        matrices.pop();

        float fluidHeight = buffer.getTotalAmount() / (float) buffer.getCapacity() * maxHeight;
        float angleOffset = (float) (Math.PI / 3);
        float angle = be.getWorld().getTime() + tickDelta;
        float offset = 0.5f;

//        VertexConsumer consumer = vertexConsumers.getBuffer(BeamEffect.BEAM_LAYER);
//        BeamRenderer.renderBeam(matrices, consumer, MinecraftClient.getInstance().cameraEntity.getEyePos(),
//                new Vec3d(58, 4, 135),
//                new Vec3d(58, 10, 133),
//        123, 171, 354, 100, 1);

        matrices.translate(-0.5, -0.5, -0.5);

        matrices.pop();
    }
}
