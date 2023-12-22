package com.neep.neepmeat.machine.pylon;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
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
        be.angle = MathHelper.wrapDegrees(be.angle + delta * be.getSpeed());
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);

        if (be.isRunning())
        {
            BERenderUtils.renderModel(NMExtraModels.PYLON_ROTOR_ACTIVE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        }
        else
        {
            BERenderUtils.renderModel(NMExtraModels.PYLON_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        }
    }
}
