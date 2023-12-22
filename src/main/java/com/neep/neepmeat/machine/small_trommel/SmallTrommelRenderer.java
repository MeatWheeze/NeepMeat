package com.neep.neepmeat.machine.small_trommel;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SmallTrommelRenderer implements BlockEntityRenderer<SmallTrommelBlockEntity>
{
    public SmallTrommelRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(SmallTrommelBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(SmallTrommelBlock.FACING);
//        be.currentSpeed = (float) (be.rotorSpeed * MathHelper.lerp(0.1, be.currentSpeed, be.getRunningRate() * 40));
//        be.angle = MathHelper.wrapDegrees(be.angle + be.currentSpeed * delta);
        float angle = 0;
        if (be.getWorld().getBlockEntity(be.getPos().offset(facing.getOpposite())) instanceof IMotorBlockEntity motor)
        {
            angle = motor.getRotorAngle();
        }

        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(angle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.SMALL_TROMMEL_MESH, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
        FluidVariant fluid = be.currentFluid;
        if (fluid != null && be.totalProgress > 0)
        {
            matrices.push();
            be.renderProgress = NMMaths.lerpForwards(0.1f, be.renderProgress, be.progress / be.totalProgress);
            matrices.translate(0.5, 0.5, 0.7);
            matrices.scale(0.6f, 0.6f, 1.7f);
            matrices.translate(-0.5, -0.5, 0);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
            renderFluidColumn(vertexConsumers, matrices, be.currentFluid, 0, 1, 1, be.renderProgress);
            matrices.pop();
        }
    }

    public static void renderFluidColumn(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float startXYZ, float depth, float endY, float scaleY)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayers.getEntityBlockLayer(Blocks.BLACK_STAINED_GLASS.getDefaultState(), false));
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;

        if (fluid.isBlank() || scaleY == 0)
        {
            return;
        }

        float startY = startXYZ;
        float dist = startY + (endY - startY) * scaleY;

        assert renderer != null;
        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        for (Direction direction : Direction.values())
        {
            if (direction == Direction.UP)
            {
                emitter.square(Direction.UP, depth, depth, 1 - depth, 1 - depth, 1 - dist);
            }
            else if (direction != Direction.DOWN)
            {
                emitter.square(direction, depth, startY, 1 - depth, dist, depth);
            }

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
        }
    }
}
