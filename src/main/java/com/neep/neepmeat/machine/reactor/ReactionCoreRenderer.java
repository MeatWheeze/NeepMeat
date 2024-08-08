package com.neep.neepmeat.machine.reactor;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class ReactionCoreRenderer implements BlockEntityRenderer<ReactionCoreBlockEntity>
{
    public ReactionCoreRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(ReactionCoreBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        float radius = (float) entity.clientIncidentZoneRadius;
        float radius = 10;

        // Sphere time!

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEndPortal());

        matrices.translate(0.5, 0.5, 0.5);
        Matrix4f top = matrices.peek().getPositionMatrix();

        int lat = 20;
        int lon = 20;

        for (int i = 0; i < lat; ++i)
        {
            float yaw1 = MathHelper.TAU * ((float) i / lat);
            float yaw2 = MathHelper.TAU * ((float) (i + 1) / lat);

            // Only make half a wedge.
            for (int j = 0; j < lon / 2; ++j)
            {
                // Subtract half pi to rotate the wedge
                float pitch1  = MathHelper.TAU * ((float) j / lon) - MathHelper.HALF_PI;
                float pitch2  = MathHelper.TAU * ((float) (j + 1) / lon) - MathHelper.HALF_PI;

                float xzLen1 = radius * MathHelper.cos(pitch1);
                float xzLen2 = radius * MathHelper.cos(pitch2);

                float y1 = radius * MathHelper.sin(pitch1);
                float y2 = radius * MathHelper.sin(pitch2);

                float x1 = xzLen1 * MathHelper.cos(yaw1);
                float x2 = xzLen1 * MathHelper.cos(yaw2);
                float z1 = xzLen1 * MathHelper.sin(yaw1);
                float z2 = xzLen1 * MathHelper.sin(yaw2);

                float x3 = xzLen2 * MathHelper.cos(yaw1);
                float x4 = xzLen2 * MathHelper.cos(yaw2);
                float z3 = xzLen2 * MathHelper.sin(yaw1);
                float z4 = xzLen2* MathHelper.sin(yaw2);

                consumer.vertex(top, x2, y1, z2).next();
                consumer.vertex(top, x1, y1, z1).next();
                consumer.vertex(top, x3, y2, z3).next();
                consumer.vertex(top, x4, y2, z4).next();
            }
        }

    }

    @Override
    public boolean rendersOutsideBoundingBox(ReactionCoreBlockEntity blockEntity)
    {
        return true;
    }
}
