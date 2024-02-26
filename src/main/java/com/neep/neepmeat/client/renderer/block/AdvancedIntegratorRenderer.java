package com.neep.neepmeat.client.renderer.block;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class AdvancedIntegratorRenderer implements BlockEntityRenderer<AdvancedIntegratorBlockEntity>
{
    public AdvancedIntegratorRenderer(BlockEntityRendererFactory.Context context)
    {

    }

    @Override
    public void render(AdvancedIntegratorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        long time = entity.getWorld().getTime();
        double div = 20;
        float offset = (float) (Math.sin(time / div) * Math.cos(tickDelta / div) + Math.sin(tickDelta / div) * Math.cos(time / div));
        matrices.push();
        matrices.translate(0, 4 + offset * 0.1, 0);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45));
        matrices.translate(-0.5, -0.5, -0.5);
        matrices.scale(1, 2, 1);

        BERenderUtils.renderModelSmooth(
                NMExtraModels.ADVANCED_INTEGRATOR_MEAT,
                matrices,
                entity.getWorld(),
                entity.getPos().up(2),
                entity.getCachedState(),
                vertexConsumers
        );
        matrices.pop();

        matrices.translate(0, 1, 0);
        matrices.scale(1, 1 + offset * 0.05f, 1);
        matrices.translate(0, -1, 0);
        matrices.translate(0, 2, 0);
        BERenderUtils.renderModelSmooth(
                NMExtraModels.ADVANCED_INTEGRATOR_DANGLIES,
                matrices,
                entity.getWorld(),
                entity.getPos().up(2),
                entity.getCachedState(),
                vertexConsumers
        );
    }
}
