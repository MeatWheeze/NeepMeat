package com.neep.neepmeat.machine.trough;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.machine.crucible.CrucibleRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
@SuppressWarnings("UnstableApiUsage")
public class TroughRenderer implements BlockEntityRenderer<TroughBlockEntity>
{
    public TroughRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(TroughBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        WritableSingleFluidStorage storage = be.getStorage(null);
        storage.renderLevel = (float) MathHelper.lerp(0.1, storage.renderLevel, storage.amount / (float) storage.getCapacity());
        CrucibleRenderer.renderSurface(vertexConsumers, matrices, be.getStorage(null).getResource(), 1 / 16f, 9 / 16f, 1 / 16f, storage.renderLevel);
    }
}
