package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.block.StopValveBlock;
import com.neep.neepmeat.block.pipe.AbstractAxialPipe;
import com.neep.neepmeat.blockentity.StopValveBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

public class StopValveRenderer<T extends StopValveBlockEntity> implements BlockEntityRenderer<T>
{
    public StopValveRenderer(BlockEntityRendererFactory.Context context)
    {

    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(AbstractAxialPipe.FACING);
        boolean open = be.getCachedState().get(StopValveBlock.OPEN);

        matrices.push();
        rotateFacing(facing, matrices);


        be.openDelta = (float) MathHelper.lerp(0.1, be.openDelta, open ? 0 : 1);
        matrices.translate(0, be.openDelta * - 1 / 16f, 0);

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.openDelta * 135));
        matrices.translate(-0.5, -0.5, -0.5);

        renderModel(NMExtraModels.VALVE_WHEEL, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();
    }

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

    public static void rotateFacing(Direction facing, MatrixStack matrices)
    {
        matrices.translate(0.5, 0.5, 0.5);
        switch (facing)
        {
            case NORTH:
            {
                break;
            }
            case EAST:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                break;
            }
            case SOUTH:
            {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
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
