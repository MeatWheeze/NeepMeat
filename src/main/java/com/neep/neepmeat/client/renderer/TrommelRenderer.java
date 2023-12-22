package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.block.base.BaseHorFacingBlock;
import com.neep.neepmeat.blockentity.TrommelBlockEntity;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.init.BlockInitialiser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class TrommelRenderer<T extends TrommelBlockEntity> implements BlockEntityRenderer<T>
{
    Model model;

    public TrommelRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        Direction facing = be.getCachedState().get(BaseHorFacingBlock.FACING);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(facing.asRotation()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(be.getWorld().getTime() + tickDelta * 1));
        matrices.translate(-0.5, -0.5, -0.5);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
                BlockInitialiser.TROMMEL_CENTRE.getDefaultState(),
                be.getPos(),
                be.getWorld(),
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                true,
                new Random());
        matrices.pop();

        ItemStack stack = be.getResource().toStack((int) be.getAmount());

//        be.stackRenderDelta = MathHelper.lerp(delta, be.stackRenderDelta, be.getAmount() <= 0 ? 0.3f : 0f);
        matrices.translate(0.5, 0.25f, 0.5);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((be.getWorld().getTime() + tickDelta) * 1));


        matrices.pop();
    }
}