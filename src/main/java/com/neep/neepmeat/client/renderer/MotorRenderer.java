package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.blockentity.machine.MotorBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class MotorRenderer implements BlockEntityRenderer<MotorBlockEntity>
{
    public MotorRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MotorBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseFacingBlock.FACING);

        long time = be.getWorld().getTime();

        be.currentSpeed = (float) (be.rotorSpeed * MathHelper.lerp(0.5, be.currentSpeed, be.running ? 1 : 0));

        BERenderUtils.rotateFacingSouth(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Z.getRadialQuaternion((be.getWorld().getTime() + tickDelta) * be.currentSpeed));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.MOTOR_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }
}
