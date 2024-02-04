package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
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
        Vec3d tipTarget = be.getTarget(tickDelta);
        double targetX = tipTarget.x;
        double targetY = tipTarget.y;
        double targetZ = tipTarget.z;

        double relX = targetX - (be.getPos().getX() + 0.5);
        double relY = targetY - (be.getPos().getY() + 1 + 2 / 16f);
        double relZ = targetZ - (be.getPos().getZ() + 0.5);

        float yaw = (float) Math.toDegrees(Math.atan2(relX, relZ));

        float l = 2 + 5 / 16f;
        double lx = Math.sqrt(relX * relX + relZ * relZ);
        double ly = relY;

        double d5 = Math.sqrt(lx * lx + ly * ly);

        // Constrain
        double x1 = lx / d5 * Math.min(d5, 2 * l - 0.0001);
        double y1 = ly / d5 * Math.min(d5, 2 * l - 0.0001);

        double d = Math.sqrt((x1 * x1 + y1 * y1));

        double x2 = x1 / d;
        double y2 = y1 / d;

        // ???
        double a = Math.acos((-(d * d)) / (-2 * d * l));

        // Rotation matrix
        double x3 = l * (x2 * cos(a) - y2 * sin(a));
        double y3 = l * (x2 * sin(a) + y2 * cos(a));

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        matrices.translate(-0.5, -0.5, -0.5);

        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SPINNY_BIT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);

        matrices.push();
        matrices.translate(0, 2 - 1 / 16f, 0);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle1 = Math.atan2(x3, y3);
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) angle1));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SEGMENT_1, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();

        matrices.translate(0, y3 + 2 - 1 / 16f, x3);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle2 = Math.atan2((lx - x3), (ly - y3));
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) angle2));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SEGMENT_2, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();

    }
}
