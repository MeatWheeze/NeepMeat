package com.neep.neepmeat.client.renderer.block;

import com.neep.neepmeat.machine.live_machine.block.LargeTrommelBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.LargeTrommelBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
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
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class LargeTrommelRenderer implements BlockEntityRenderer<LargeTrommelBlockEntity>
{
    public LargeTrommelRenderer(BlockEntityRendererFactory.Context context)
    {

    }

    @Override
    public void render(LargeTrommelBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay)
    {
        FluidVariant fluidVariant = be.getStorage().variant;
        Direction facing = be.getCachedState().get(LargeTrommelBlock.FACING);
        if (!fluidVariant.isBlank() && be.progressIncrement() > 0)
        {
            float progress = (be.getWorld().getTime() - be.getStorage().recipeStartTime) / (be.getStorage().totalProgress / be.progressIncrement());

            matrices.translate(0.5, 0.5, 0.5);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.asRotation()));
            matrices.translate(-0.5, -0.5, -0.5);
            matrices.translate(-0.5, -0.2, 1);
            renderFluidColumn(vcp, matrices, fluidVariant, 1 - progress, light);
        }
    }

    public static void renderFluidColumn(VertexConsumerProvider vcp, MatrixStack matrices, FluidVariant fluid, float level, int light)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        if (sprite == null)
            return;

        VertexConsumer consumer = vcp.getBuffer(RenderLayers.getEntityBlockLayer(Blocks.BLACK_STAINED_GLASS.getDefaultState(), false));

        int col = FluidVariantRendering.getColor(fluid);

        float cr = ((col >> 16) & 255) / 256f;
        float cg = ((col >> 8) & 255) / 256f;
        float cb = (col & 255) / 256f;

        if (fluid.isBlank() || level == 0)
        {
            return;
        }

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        assert renderer != null;
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        float y0 = 0.7f;
        float y1 = 0.7f + 0.5f * level;
        float x0 = -0.2f;
        float x1 = 1.2f;
        float x2 = -0.4f;
        float x3 = 1.4f;

        // Needs three sections to cover the entire length without stretching the texture.
        for (int i = 0; i < 3; ++i)
        {
            float z = i;

            // Top face
            emitter.pos(0, new Vector3f(x1, y1, z + 0)).uv(0, u1, v0);
            emitter.pos(1, new Vector3f(x0, y1, z + 0)).uv(1, u0, v0);
            emitter.pos(2, new Vector3f(x0, y1, z + 1)).uv(2, u0, v1);
            emitter.pos(3, new Vector3f(x1, y1, z + 1)).uv(3, u1, v1);
            BakedQuad quad = emitter.toBakedQuad(sprite);
            consumer.quad(matrices.peek(), quad, cr, cg, cb, light, OverlayTexture.DEFAULT_UV);
            emitter.emit();

            // Right face (looking from the trommel's controller block)
            emitter.pos(0, new Vector3f(x0, y1, z + 0)).uv(0, u1, v0);
            emitter.pos(1, new Vector3f(x2, y0, z + 0)).uv(1, u0, v0);
            emitter.pos(2, new Vector3f(x2, y0, z + 1)).uv(2, u0, v1);
            emitter.pos(3, new Vector3f(x0, y1, z + 1)).uv(3, u1, v1);
            quad = emitter.toBakedQuad(sprite);
            consumer.quad(matrices.peek(), quad, cr, cg, cb, light, OverlayTexture.DEFAULT_UV);
            emitter.emit();

            // Left face
            emitter.pos(1, new Vector3f(x1, y1, z + 0)).uv(1, u1, v0);
            emitter.pos(0, new Vector3f(x3, y0, z + 0)).uv(0, u0, v0);
            emitter.pos(3, new Vector3f(x3, y0, z + 1)).uv(3, u0, v1);
            emitter.pos(2, new Vector3f(x1, y1, z + 1)).uv(2, u1, v1);
            quad = emitter.toBakedQuad(sprite);
            consumer.quad(matrices.peek(), quad, cr, cg, cb, light, OverlayTexture.DEFAULT_UV);
            emitter.emit();
        }
    }
}
