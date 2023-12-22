package com.neep.neepmeat.machine.pylon;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class PylonRenderer implements BlockEntityRenderer<PylonBlockEntity>
{
    public PylonRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(PylonBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.translate(0.5, 0.5, 0.5);
        float delta = !MinecraftClient.getInstance().isPaused() ? MinecraftClient.getInstance().getLastFrameDuration() : 0;
        be.angle = MathHelper.wrapDegrees(be.angle + delta * 1);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.PYLON_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
