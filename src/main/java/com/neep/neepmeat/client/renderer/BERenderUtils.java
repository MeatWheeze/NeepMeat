package com.neep.neepmeat.client.renderer;

import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

public class BERenderUtils
{
    public static void renderModel(Identifier model, MatrixStack matrices, World world, BlockPos pos, BlockState state, VertexConsumerProvider vertexConsumers)
    {
        BakedModelManager manager = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelManager();
        BakedModel handle = BakedModelManagerHelper.getModel(manager, model);
        BlockModelRenderer renderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        renderer.render(
                world,
                handle,
                state,
                pos,
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                true,
                new Random(0),
                0,
                0
        );
    }

    /**
     * @param facing The JSON model must be facing North by default.
     * @param matrices Remember to push and pop.
     */
    public static void rotateFacing(Direction facing, MatrixStack matrices)
    {
        matrices.translate(0.5, 0.5, 0.5);
        switch (facing)
        {
            case NORTH:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                break;
            }
            case EAST:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                break;
            }
            case SOUTH:
            {
                break;
            }
            case WEST:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
                break;
            }
            case UP:
            {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                break;
            }
            case DOWN:
            {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            }
        }
        matrices.translate(-0.5, -0.5, -0.5);
    }
}
