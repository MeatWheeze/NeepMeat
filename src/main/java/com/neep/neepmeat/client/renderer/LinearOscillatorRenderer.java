package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.breaker.LinearOscillatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class LinearOscillatorRenderer implements BlockEntityRenderer<LinearOscillatorBlockEntity>
{
    public LinearOscillatorRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(LinearOscillatorBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseFacingBlock.FACING);


        be.clientExtension = (float) MathHelper.lerp(0.3, be.clientExtension, be.extension);
        BERenderUtils.rotateFacingSouth(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
//        long clientCooldown = be.getWorld().getTime() - be.extensionTime;
//        float extension = (1 - (clientCooldown + tickDelta) / (float) be.maxCooldown);
//        float extension = MathHelper.lerp(tickDelta, be.prevExtension, be.extension) * 0.8f;
        float maxExtension = 1 / 16f * 9;
        matrices.translate(0, 0, be.clientExtension * maxExtension);
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.LO_ARMATURE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }
}
