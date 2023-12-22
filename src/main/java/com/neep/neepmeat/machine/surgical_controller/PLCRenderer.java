package com.neep.neepmeat.machine.surgical_controller;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class PLCRenderer implements BlockEntityRenderer<PLCBlockEntity>
{
    public PLCRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(PLCBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        be.getRobot().clientX = MathHelper.lerp(0.1d, be.getRobot().clientX, be.getRobot().getX());
        be.getRobot().clientY = MathHelper.lerp(0.1d, be.getRobot().clientY, be.getRobot().getY());
        be.getRobot().clientZ = MathHelper.lerp(0.1d, be.getRobot().clientZ, be.getRobot().getZ());
        matrices.translate(
                be.getRobot().clientX - be.getPos().getX() - 0.5,
                be.getRobot().clientY - be.getPos().getY() - 0.5,
                be.getRobot().clientZ - be.getPos().getZ() - 0.5);
        Direction facing = be.getCachedState().get(TableControllerBlock.FACING);
        matrices.translate(0, 0.5 + (be.getRobot().isActive() ? 0.05 * Math.sin((be.getWorld().getTime() + tickDelta) / 10f) : 0), 0);
        BERenderUtils.rotateFacing(facing, matrices);
        BERenderUtils.renderModel(NMExtraModels.SURGERY_ROBOT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }
}