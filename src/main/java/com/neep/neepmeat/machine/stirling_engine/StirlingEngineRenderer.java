package com.neep.neepmeat.machine.stirling_engine;

import com.eliotlash.mclib.utils.MathHelper;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class StirlingEngineRenderer implements BlockEntityRenderer<StirlingEngineBlockEntity>
{
    public static double LAST_FRAME;
    public static double CURRENT_FRAME;

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

        CURRENT_FRAME = be.getWorld().getTime() + tickDelta;
        float delta = (float) (CURRENT_FRAME - LAST_FRAME);
//        this.lastFrame = currentFrame;

        // Temporal discretisation!
        be.angle = MathHelper.wrapDegrees(be.angle + StirlingEngineBlockEntity.energyToSpeed(be.energyStored) * delta);

//        float angle = MathHelper.wrapDegrees((be.getWorld().getTime() + tickDelta) * be.speed);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(be.angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.STIRLING_ENGINE_ROTOR, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }

    static
    {
        WorldRenderEvents.START.register(((context) ->
        {
            LAST_FRAME = CURRENT_FRAME;
        }));
    }
}
