package com.neep.neepmeat.machine.surgical_controller;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
public class PLCRenderer implements BlockEntityRenderer<PLCBlockEntity>
{
    public PLCRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(PLCBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        var robot = be.getRobot();
        robot.prevX = robot.clientX;
        robot.prevY = robot.clientY;
        robot.prevZ = robot.clientZ;

        // Smooth the robot's position
        robot.clientX = MathHelper.lerp(0.1d, robot.clientX, robot.getX());
        robot.clientY = MathHelper.lerp(0.1d, robot.clientY, robot.getY());
        robot.clientZ = MathHelper.lerp(0.1d, robot.clientZ, robot.getZ());
        robot.clientYaw = MathHelper.lerpAngleDegrees(0.1f, robot.clientYaw, robot.getYaw());

        double vx = robot.clientX - robot.prevX;
        double vy = robot.clientY - robot.prevY;
        double vz = robot.clientZ - robot.prevZ;

        double speed = (float) Math.sqrt(vx * vx + vz * vz);

        // Only render the robot in 3rd person
        PLCHudRenderer plcHudRenderer = PLCHudRenderer.getInstance();
        if (plcHudRenderer == null || plcHudRenderer.getBlockEntity() != be)
        {
            matrices.push();
            matrices.translate(
                    robot.clientX - be.getPos().getX() - 0.5,
                    robot.clientY - be.getPos().getY() - 0.5,
                    robot.clientZ - be.getPos().getZ() - 0.5);
            
            matrices.translate(0, 0.5 + (robot.isActive() ? 0.05 * Math.sin((be.getWorld().getTime() + tickDelta) / 10f) : 0), 0);

            matrices.translate(0.5, 0.5, 0.5);
//            matrices.multiply(normal.getDegreesQuaternion((float) (100000 * speed)));
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(robot.clientYaw + 180));
            matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion((float) (100 * speed)));
            matrices.translate(-0.5, -0.5, -0.5);

//            BERenderUtils.rotateFacing(facing, matrices);
            BERenderUtils.renderModel(NMExtraModels.SURGERY_ROBOT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
            matrices.pop();
        }
    }
}