package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.transfer.MultiFluidBuffer;
import com.neep.neepmeat.block.vat.VatControllerBlock;
import com.neep.neepmeat.blockentity.machine.VatControllerBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

public class VatRenderer implements BlockEntityRenderer<VatControllerBlockEntity>
{
    public VatRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(VatControllerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        MultiFluidBuffer buffer = ((MultiFluidBuffer) be.getFluidStorage());
        Direction facing = be.getCachedState().get(VatControllerBlock.FACING);
        float maxHeight = 2;

        matrices.push();

        matrices.translate( 0.5 - facing.getOffsetX(), 1, 0.5 - facing.getOffsetZ());

        matrices.push();
        matrices.translate(-1.5, 0, -1.5);
        MultiFluidRenderer.renderMultiFluid(buffer, 1, maxHeight, matrices, vertexConsumers, light, overlay);
        matrices.pop();

        float fluidHeight = buffer.getTotalAmount() / (float) buffer.getCapacity() * maxHeight;
        float angleOffset = (float) (Math.PI / 3);
        float angle = be.getWorld().getTime() + tickDelta;
        float offset = 0.5f;
        Transaction transaction = Transaction.openOuter();

        for (StorageView<ItemVariant> view : be.getItemStorage().iterable(transaction))
        {
            matrices.push();
            matrices.multiply(Quaternion.fromEulerXyz(0, (angle * angleOffset * 2) / 20, 0));
            float height = (float) ((Math.sin(angle / 20 + angleOffset) + 0.4) / 2 * fluidHeight) + 0.2f;
            matrices.translate(0, height, offset);

            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(view.getResource().toStack((int) view.getAmount()), ModelTransformation.Mode.GROUND, 255, overlay, matrices, vertexConsumers, 0);
            angleOffset += Math.PI / 3;
            matrices.pop();
        }
        transaction.abort();

//        VertexConsumer consumer = vertexConsumers.getBuffer(BeamEffect.BEAM_LAYER);
//        BeamRenderer.renderBeam(matrices, consumer, MinecraftClient.getInstance().cameraEntity.getEyePos(),
//                new Vec3d(58, 4, 135),
//                new Vec3d(58, 10, 133),
//        123, 171, 354, 100, 1);

        matrices.translate(-0.5, -0.5, -0.5);

        matrices.pop();
    }
}
