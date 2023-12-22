package com.neep.neepmeat.machine.death_blades;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class DeathBladesRenderer implements BlockEntityRenderer<DeathBladesBlockEntity>
{
    public DeathBladesRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(DeathBladesBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        float angle = MathHelper.wrapDegrees(be.getWorld().getTime() + tickDelta);

        matrices.push();
        Direction facing = be.getCachedState().get(DeathBladesBlock.FACING);
        be.clientAngle = MathHelper.lerpAngleDegrees(0.5f, be.clientAngle, be.angle);
        renderBlade(be, matrices, facing, vertexConsumers, be.clientAngle, 0);
        renderBlade(be, matrices, facing, vertexConsumers, 360 - be.clientAngle, 2);
        matrices.pop();
    }

    public static void renderBlade(DeathBladesBlockEntity be, MatrixStack matrices, Direction facing, VertexConsumerProvider vertexConsumers, float angle, float offset)
    {
        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
        matrices.translate( 0, 0, - offset / 16f);
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.LARGE_BLADE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }
}
