package com.neep.neepmeat.machine.small_trommel;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SmallTrommelRenderer implements BlockEntityRenderer<SmallTrommelBlockEntity>
{
    public SmallTrommelRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(SmallTrommelBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(SmallTrommelBlock.FACING);
//        be.currentSpeed = (float) (be.rotorSpeed * MathHelper.lerp(0.1, be.currentSpeed, be.getRunningRate() * 40));
//        be.angle = MathHelper.wrapDegrees(be.angle + be.currentSpeed * delta);
        float angle = 0;
        if (be.getWorld().getBlockEntity(be.getPos().offset(facing.getOpposite())) instanceof IMotorBlockEntity motor)
        {
            angle = motor.getRotorAngle();
        }

        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.SMALL_TROMMEL_MESH, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
