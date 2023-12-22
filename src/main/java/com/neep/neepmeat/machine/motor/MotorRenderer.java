package com.neep.neepmeat.machine.motor;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class MotorRenderer implements BlockEntityRenderer<MotorBlockEntity>
{
    protected static long LAST_WORLD_TIME;
    protected static float LAST_TICK_DELTA;
    protected static long WORLD_TIME;
    protected static float CURRENT_TICK_DELTA;
    public static double DELTA;

    public MotorRenderer(BlockEntityRendererFactory.Context ctx)
    {
//        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((context, hitResult) ->
//        {
//            this.lastWorldTime = this.worldTime;
//            this.lastTickDelta = this.currentTickDelta;
//            return true;
//        }));
    }

    @Override
    public void render(MotorBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseFacingBlock.FACING);

//        this.currentFrame = be.getWorld().getTime() + tickDelta;
//        double delta = (worldTime - lastWorldTime) + (currentTickDelta - lastTickDelta);
//        this.lastFrame = currentFrame;

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        be.currentSpeed = (float) (be.rotorSpeed * MathHelper.lerp(0.1, be.currentSpeed, MotorBlockEntity.rateToSpeed(be.getRunningRate())));
        be.angle = MathHelper.wrapDegrees(be.angle + be.currentSpeed * delta);

        BERenderUtils.rotateFacingSouth(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.MOTOR_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }

    static
    {
//        WorldRenderEvents.START.register(((context) ->
//        {
//            MinecraftClient client = MinecraftClient.getInstance();
//            WORLD_TIME = client.world.getTime();
//            CURRENT_TICK_DELTA = client.getTickDelta();
//            DELTA = (WORLD_TIME - LAST_WORLD_TIME) + (CURRENT_TICK_DELTA - LAST_TICK_DELTA);
//        }));
//
//        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((context, hitResult) ->
//        {
//            LAST_WORLD_TIME = WORLD_TIME;
//            LAST_TICK_DELTA = CURRENT_TICK_DELTA;
//            return true;
//        }));
    }
}
