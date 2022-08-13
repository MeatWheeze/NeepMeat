package com.neep.neepmeat.machine.casting_basin;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.machine.crucible.CrucibleRenderer;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("UnstableApiUsage")
public class CastingBasinRenderer implements BlockEntityRenderer<CastingBasinBlockEntity>
{
    public CastingBasinRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(CastingBasinBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        WritableSingleFluidStorage storage = be.getStorage().inputStorage;
        FluidVariant variant = storage.getResource();
        storage.renderLevel = MathHelper.lerp(0.1f, storage.renderLevel, storage.getAmount() / (float) storage.getCapacity());
        renderSurface(vertexConsumers, matrices, variant, 10 / 16f, 3 / 16f, 3 / 16f, storage.renderLevel);
    }

    public static void renderSurface(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float start, float height, float depth, float scale)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayer.getTranslucent());
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        // Magic colourspace transformation copied from Modern Industrialisation
        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;

        if (fluid.isBlank() || scale == 0)
        {
            return;
        }

        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        emitter.square(Direction.UP, depth, depth, 1 - depth, 1 - depth, 1 - (start + height * scale));

        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
    }
}
