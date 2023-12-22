package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.transport.block.fluid_transport.entity.FilterPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value = EnvType.CLIENT)
public class StopValveRenderer<T extends FilterPipeBlockEntity> implements BlockEntityRenderer<T>
{
    public StopValveRenderer(BlockEntityRendererFactory.Context context)
    {

    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        Direction facing = be.getCachedState().get(AbstractAxialPipe.FACING);
//        boolean open = be.getCachedState().get(StopValveBlock.OPEN);
//
        matrices.push();
//        BERenderUtils.rotateFacingSouth(facing, matrices);
//
//
//        be.openDelta = (float) MathHelper.lerp(0.1, be.openDelta, open ? 0 : 1);
//        matrices.translate(0, be.openDelta * - 1 / 16f, 0);
//
//        matrices.translate(0.5, 0.5, 0.5);
//        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.openDelta * 135));
//        matrices.translate(-0.5, -0.5, -0.5);
//
//        BERenderUtils.renderModel(NMExtraModels.VALVE_WHEEL, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
//
        matrices.pop();
    }

}
