package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.mixin.BlockModelRendererAccessor;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class BERenderUtils
{
    public static void renderModel(Identifier model, MatrixStack matrices, World world, BlockPos pos, BlockState state, VertexConsumerProvider vertexConsumers)
    {
        BakedModelManager manager = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelManager();
        BakedModel handle = BakedModelManagerHelper.getModel(manager, model);
        BlockModelRenderer renderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();

        renderFlat(
                renderer,
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

    public static void renderFlat(BlockModelRenderer renderer, BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay)
    {
        BitSet bitSet = new BitSet(3);
        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            renderQuadsFlat(renderer, world, state, pos, -1, overlay, true, matrices, vertexConsumer, list2, bitSet);
        }
    }

    public static void renderQuadsFlat(BlockModelRenderer renderer, BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, BitSet flags) {
        BlockModelRendererAccessor accessor = (BlockModelRendererAccessor) renderer;
        for (BakedQuad bakedQuad : quads) {
            if (useWorldLight) {
                light = WorldRenderer.getLightmapCoordinates(world, state, pos);
            }
            MatrixStack.Entry entry = matrices.peek();

//            Vec3i vec3i = bakedQuad.getFace().getVector();
//            Vec3f vec3f = new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
//            vec3f.transform(entry.getNormalMatrix());
//            Direction newFace = nearestDirection(vec3f);

            float f = 1;
            accessor.callRenderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, f, f, f, f, light, light, light, light, overlay);
        }
    }

    public static Direction nearestDirection(Vec3f vec)
    {
        Direction closest = Direction.NORTH;
        float closestDist = 100;
        for (Direction direction : Direction.values())
        {
            Vec3f vec1 = closest.getUnitVector();
            vec1.subtract(vec);
            float len = length(vec1);
            if (len < closestDist)
            {
                closest = direction;
                closestDist = len;
            }
        }
        return Direction.UP;
    }

    public static float length(Vec3f vec)
    {
        float u = vec.getX();
        float v = vec.getY();
        float w = vec.getZ();
        return (float) Math.sqrt(u * u + v * v + w * w);
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
            case SOUTH:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                break;
            }
            case WEST:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                break;
            }
            case NORTH:
            {
                break;
            }
            case EAST:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
                break;
            }
            case DOWN:
            {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                break;
            }
            case UP:
            {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            }
        }
        matrices.translate(-0.5, -0.5, -0.5);
    }

    public static void rotateFacingSouth(Direction facing, MatrixStack matrices)
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
