package com.neep.neepmeat.machine.assembler;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value= EnvType.CLIENT)
public class AssemblerRenderer implements BlockEntityRenderer<AssemblerBlockEntity>
{

    public AssemblerRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(AssemblerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(AssemblerBlock.FACING);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
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
