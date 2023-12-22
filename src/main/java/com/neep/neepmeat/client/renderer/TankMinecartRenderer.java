package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.entity.TankMinecartEntity;
import com.neep.neepmeat.init.NMBlocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

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

//        matrices.push();
//        float p = 0.75f;
//        matrices.scale(0.75f, 0.75f, 0.75f);
//        matrices.translate(-0.5, (float)(minecart.getBlockOffset() - 8) / 16.0f, 0.5);
//        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
//        renderBlock(minecart, f, NMBlocks.TANK.getDefaultState(), matrices, vertexConsumerProvider, i);
//
//        matrices.pop();
    }
}
