package com.neep.neepmeat.machine.stirling_engine;

import com.eliotlash.mclib.utils.MathHelper;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.motor.MotorBlockEntity;
import com.neep.neepmeat.machine.motor.MotorRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
public class StirlingEngineRenderer implements BlockEntityRenderer<StirlingEngineBlockEntity>
{
    public StirlingEngineRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(StirlingEngineBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(StirlingEngineBlock.FACING);
        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);

        matrices.translate(0.5, 0.5, 0.5);

        // Temporal discretisation!
        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        be.angle = MathHelper.wrapDegrees(be.angle + be.getSpeed() * delta);

//        float angle = MathHelper.wrapDegrees((be.getWorld().getTime() + tickDelta) * be.speed);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.STIRLING_ENGINE_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }

    static
    {
    }
}
