package com.neep.neepmeat.transport.client.renderer;

import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.transport.block.fluid_transport.WindowPipeBlock;
import com.neep.neepmeat.transport.block.fluid_transport.entity.WindowPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class WindowPipeRenderer implements BlockEntityRenderer<WindowPipeBlockEntity>
{
    public WindowPipeRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(WindowPipeBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        WindowPipeBlockEntity.WindowPipeVertex vertex = be.getPipeVertex();
        FluidVariant variant = be.clientVariant;
        be.clientFraction = MathHelper.lerp(0.1f, be.clientFraction, (float) be.clientAmount / vertex.getCapacity());

        BERenderUtils.rotateFacing(be.getCachedState().get(WindowPipeBlock.FACING), matrices);
        renderFluidCuboid(vertexConsumers, matrices, variant, be.clientFraction, light);
    }

    public static void renderFluidCuboid(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float fraction, int light)
    {
        if (fluid == null || fluid.isBlank() || fraction == 0) return;

        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayer.getTranslucent());
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;


        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        for (Direction direction : Direction.values())
        {
            if (direction.getAxis().equals(Direction.NORTH.getAxis()))
            {
//                emitter.square(direction, fraction, fraction, 1 - fraction, 1 - fraction, direction == Direction.UP ? 1 - dist : startY);
            }
            else if (direction == Direction.WEST || direction == Direction.EAST)
            {
                emitter.square(direction,  5 / 16f, (8 / 16f) - (3 / 16f) * fraction, (16 - 5) / 16f, (8 / 16f) + (3 / 16f) * fraction, (8 / 16f) - (3 / 16f) * fraction);
            }
            else if (direction == Direction.UP || direction == Direction.DOWN)
            {
                emitter.square(direction,  (8 / 16f) - (3 / 16f) * fraction, 5 / 16f, (8 / 16f) + (3 / 16f) * fraction, (16 - 5) / 16f, (8 / 16f) - (3 / 16f) * fraction);
            }

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, light, OverlayTexture.DEFAULT_UV);
        }
    }
}