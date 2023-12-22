package com.neep.assembly.client.renderer;

import com.neep.assembly.AssemblyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.PalettedContainer;

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

        PalettedContainer<BlockState> container = entity.getPalette();

        matrices.push();
        for (int i = 0; i < 16; ++i)
        {
            matrices.push();
            for (int j = 0; j < 16; ++j)
            {
                matrices.push();
                for (int k = 0; k < 16; ++k)
                {
                    BlockState state = container.get(i, j, k);

                    if (!state.isAir())
                    {
                        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(
                                container.get(i, j, k),
//                                state,
                                entity.getBlockPos(),
                                entity.world, matrices,
                                vertexConsumers.getBuffer(RenderLayer.getTranslucent()),
                                false,
                                new Random(0));
                    }
                    matrices.translate(0, 0, 1);
                }
                matrices.pop();
                matrices.translate(0, 1, 0);
            }
            matrices.pop();
            matrices.translate(1, 0, 0);
        }
        matrices.pop();


        int light2 = entity.world.getLightLevel(entity.getBlockPos().up());

//        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.STONE.getDefaultState(), matrices,
//                vertexConsumers,
//                 light, OverlayTexture.DEFAULT_UV);
        matrices.pop();

//        System.out.println(entity.getBlockPos());
    }
}
