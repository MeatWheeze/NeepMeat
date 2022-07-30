package com.neep.neepmeat.machine.stirling_engine;

import com.eliotlash.mclib.utils.MathHelper;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class StirlingEngineRenderer implements BlockEntityRenderer<StirlingEngineBlockEntity>
{
    public double lastFrame;
    public double currentFrame;

    public StirlingEngineRenderer(BlockEntityRendererFactory.Context ctx)
    {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((context, hitResult) ->
        {
            this.lastFrame = this.currentFrame;
            return true;
        }));
    }

    @Override
    public void render(StirlingEngineBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(StirlingEngineBlock.FACING);
        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);

        matrices.translate(0.5, 0.5, 0.5);

        this.currentFrame = be.getWorld().getTime() + tickDelta;
        float delta = (float) (currentFrame - lastFrame);
//        this.lastFrame = currentFrame;

        be.angle = MathHelper.wrapDegrees(be.angle + be.speed * delta);

//        float angle = MathHelper.wrapDegrees((be.getWorld().getTime() + tickDelta) * be.speed);
        matrices.multiply(Vec3f.NEGATIVE_Z.getRadialQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.STIRLING_ENGINE_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }
}
