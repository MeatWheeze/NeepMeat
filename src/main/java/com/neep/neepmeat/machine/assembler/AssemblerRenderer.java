package com.neep.neepmeat.machine.assembler;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AssemblerRenderer implements BlockEntityRenderer<AssemblerBlockEntity>
{
    protected float lastFrame;
    protected float currentFrame;

    public AssemblerRenderer(BlockEntityRendererFactory.Context ctx)
    {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((context, hitResult) ->
        {
            this.lastFrame = this.currentFrame;
            return true;
        }));
    }

    @Override
    public void render(AssemblerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(AssemblerBlock.FACING);

        this.currentFrame = be.getWorld().getTime() + tickDelta;
        float delta = (currentFrame - lastFrame);

        be.currentSpeed = (float) (MathHelper.lerp(0.5, be.currentSpeed, be.spinTicks > 0 ? 15 : 0));
        if (be.spinTicks > 0)
        {
            int ooe = 1;
        }
        be.angle = MathHelper.wrapDegrees(be.angle + be.currentSpeed * delta);

        matrices.translate(0, 1 + 3 / 16f, 0);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(1.1f, 1, 1.1f);
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        BERenderUtils.renderModel(NMExtraModels.MOTOR_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

    }
}
