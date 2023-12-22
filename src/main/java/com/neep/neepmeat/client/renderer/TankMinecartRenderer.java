package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.entity.TankMinecartEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

public class TankMinecartRenderer extends MinecartEntityRenderer<TankMinecartEntity>
{
    public TankMinecartRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer)
    {
        super(ctx, layer);
    }

    @Override
    public void render(TankMinecartEntity minecart, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        super.render(minecart, f, g, matrices, vertexConsumerProvider, i);
    }

    @Override
    protected void renderBlock(TankMinecartEntity entity, float delta, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        super.renderBlock(entity, delta, state, matrices, vertexConsumers, light);
        SingleVariantStorage<FluidVariant> storage = entity.getBuffer(null);
        FluidVariant fluid = storage.getResource();
        float scale = storage.getAmount() / (float) storage.getCapacity();
        TankMinecartEntity.renderFluidCuboid(vertexConsumers, matrices, fluid, 0.1f, 0.1f, 0.9f, 0.9f, scale);
    }

}
