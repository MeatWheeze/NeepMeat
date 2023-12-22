package com.neep.assembly.client.renderer;

import com.neep.assembly.AssemblyEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Random;

public class AssemblyRenderer extends EntityRenderer<AssemblyEntity>
{
    public AssemblyRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public Identifier getTexture(AssemblyEntity entity)
    {
        return null;
    }

    public void render(AssemblyEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        matrices.push();
        matrices.translate(-0.5, -1, -0.5);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
//                entity.getState(),
                Blocks.STONE.getDefaultState(),
                entity.getBlockPos(),
                entity.world, matrices,
                vertexConsumers.getBuffer(RenderLayer.getTranslucent()),
                false,
                new Random(0));

        int light2 = entity.world.getLightLevel(entity.getBlockPos().up());

//        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.STONE.getDefaultState(), matrices,
//                vertexConsumers,
//                 light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

//        System.out.println(entity.getBlockPos());
    }
}
