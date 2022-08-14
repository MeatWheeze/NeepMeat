package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class MotorRenderer implements BlockEntityRenderer<MotorBlockEntity>
{
    protected float lastFrame;
    protected float currentFrame;

    public MotorRenderer(BlockEntityRendererFactory.Context ctx)
    {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((context, hitResult) ->
        {
            this.lastFrame = this.currentFrame;
            return true;
        }));
    }

    @Override
    public void render(MotorBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseFacingBlock.FACING);

        this.currentFrame = be.getWorld().getTime() + tickDelta;
        float delta = (currentFrame - lastFrame);
//        this.lastFrame = currentFrame;

        be.currentSpeed = (float) (be.rotorSpeed * MathHelper.lerp(0.1, be.currentSpeed, be.getRunningRate() * 20));
        be.angle = MathHelper.wrapDegrees(be.angle + be.currentSpeed * delta);

        BERenderUtils.rotateFacingSouth(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.MOTOR_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }
}
