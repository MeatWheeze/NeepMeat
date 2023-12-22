package com.neep.neepmeat.machine.surgical_controller;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class TableControllerRenderer implements BlockEntityRenderer<TableControllerBlockEntity>
{
    public TableControllerRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(TableControllerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        be.robot.clientX = MathHelper.lerp(0.1d, be.robot.clientX, be.robot.getX());
        be.robot.clientY = MathHelper.lerp(0.1d, be.robot.clientY, be.robot.getY());
        be.robot.clientZ = MathHelper.lerp(0.1d, be.robot.clientZ, be.robot.getZ());
        matrices.translate(
                be.robot.clientX - be.getPos().getX() - 0.5,
                be.robot.clientY - be.getPos().getY() - 0.5,
                be.robot.clientZ - be.getPos().getZ() - 0.5);
        Direction facing = be.getCachedState().get(TableControllerBlock.FACING);
        matrices.translate(0, 0.5 + (be.robot.isActive() ? 0.05 * Math.sin((be.getWorld().getTime() + tickDelta) / 10f) : 0), 0);
        BERenderUtils.rotateFacing(facing, matrices);
        BERenderUtils.renderModel(NMExtraModels.SURGERY_ROBOT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }
}