package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class RoboticArmRenderer implements BlockEntityRenderer<RoboticArmBlockEntity>
{
    public RoboticArmRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(RoboticArmBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        float t = be.getWorld().getTime() + tickDelta;

        float l = 2 + 5 / 16f;
        double x = 1;
        double y = (float) (1 + Math.sin(t / 10));
//        double y = 1.5;

        double d5 = Math.sqrt(x * x + y * y);

        double x1 = x / d5 * Math.min(d5, 2 * l - 0.0001);
        double y1 = y / d5 * Math.min(d5, 2 * l - 0.0001);

        double d = Math.sqrt((x1 * x1 + y1 * y1));

        double x11 = x1 / d;
        double y11 = y1 / d;

        double a = Math.acos((-(d * d)) / (-2 * d * l));

        double d3 = l * (x11 * cos(a) - y11 * sin(a));
        double d4 = l * (x11 * sin(a) + y11 * cos(a));

        if (be.getWorld().getTime() % 4 == 0)
        {
            MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.SMOKE,
                    be.getPos().getX() + x + 0.5, (1 + 1 / 16f) + be.getPos().getY() + y, be.getPos().getZ() + 0.5,
                    0, 0, 0);

            MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.SMOKE,
                    be.getPos().getX() + d3 + 0.5, (1 + 1 / 16f) + be.getPos().getY() + d4, be.getPos().getZ() + 0.5,
                    0, 0, 0);
        }

        float yaw = 90;

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        matrices.translate(-0.5, -0.5, -0.5);

        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SPINNY_BIT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers, be.getWorld().getRandom());

        matrices.push();
        matrices.translate(0, 2 - 1 / 16f, 0);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle1 = Math.asin(d3 / Math.sqrt(d3 * d3 + d4 * d4));
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) angle1));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        BERenderUtils.renderModel(NMExtraModels.ROBOTIC_ARM_SEGMENT_1, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();

//        matrices.translate(0, 34 / 16f, 0);
        matrices.translate(0, d4 + 2 - 1 / 16f, d3);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle2 = Math.atan2((x - d3), (y - d4));
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) angle2));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        BERenderUtils.renderModel(NMExtraModels.ROBOTIC_ARM_SEGMENT_2, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();

    }
}
