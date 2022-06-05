package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.blockentity.machine.EjectorBlockEntity;
import com.neep.neepmeat.blockentity.machine.LinearOscillatorBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

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
        BERenderUtils.rotateFacing(facing, matrices);
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
