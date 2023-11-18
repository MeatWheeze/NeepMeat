package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.init.NMEntities;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class DummyEntityRenderer<T extends Entity> extends EntityRenderer<T>
{
    public DummyEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z)
    {
//        return false;
        return super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
//        BERenderUtils.renderModel(
//                NMExtraModels.EGG,
//                matrices,
//                entity.world,
//                entity.getBlockPos(),
//                Blocks.AIR.getDefaultState(), vertexConsumers);
    }

    @Override
    public Identifier getTexture(T entity)
    {
        return null;
    }
}
