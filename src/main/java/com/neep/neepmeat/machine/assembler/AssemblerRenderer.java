package com.neep.neepmeat.machine.assembler;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class AssemblerRenderer implements BlockEntityRenderer<AssemblerBlockEntity>
{
    public AssemblerRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(AssemblerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = entity.getCachedState().get(AssemblerBlock.FACING);
        matrices.translate(0, 1 + 3 / 16f, 0);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(1.1f, 1, 1.1f);
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        BERenderUtils.renderModel(NMExtraModels.MOTOR_ROTOR, matrices, entity.getWorld(), entity.getPos(), entity.getCachedState(), vertexConsumers);

    }
}
