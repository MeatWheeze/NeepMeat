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


//        be.offset = (float) MathHelper.lerp(0.3, be.offset, be.shuttle > 0 ? (float) 0.2 : 0);
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.translate(0, 0, be.extended ? 0.8 : 0);
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.LO_ARMATURE, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }
}
