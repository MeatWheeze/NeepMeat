package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.transport.block.fluid_transport.FluidBufferBlock;
import com.neep.neepmeat.transport.machine.fluid.FluidBufferBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value = EnvType.CLIENT)
public class FluidBufferRenderer implements BlockEntityRenderer<FluidBufferBlockEntity>
{
    public FluidBufferRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(FluidBufferBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        Direction facing = blockEntity.getCachedState().get(FluidBufferBlock.FACING);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.getAxis() == Direction.Axis.Z ? 90 : 0));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(facing.getAxis() == Direction.Axis.Z ? 90 : 0));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(facing.getAxis() == Direction.Axis.X ? 90 : 0));
        matrices.translate(-0.5, -0.5, -0.5);

        WritableSingleFluidStorage buffer = blockEntity.getBuffer(null);
        FluidVariant fluid = blockEntity.getBuffer(null).getResource();
        buffer.renderLevel = MathHelper.lerp(0.1f, buffer.renderLevel,(buffer.getAmount()) / (float) buffer.getCapacity());
        BERenderUtils.renderFluidCuboid(vertexConsumers, matrices, fluid, 0.1f, 0.93f, 0.3f, buffer.renderLevel, light);

        matrices.pop();
    }
}