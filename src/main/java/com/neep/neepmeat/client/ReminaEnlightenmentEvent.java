package com.neep.neepmeat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.api.enlightenment.TickingEnlightenmentEvent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class ReminaEnlightenmentEvent extends TickingEnlightenmentEvent
{
    protected ReminaEnlightenmentEvent(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
    {
        super(manager, world, player);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    private static final Identifier EYE = new Identifier(NeepMeat.NAMESPACE, "textures/world/object.png");

    static
    {
        WorldRenderEvents.BEFORE_ENTITIES.register(context ->
        {

            MatrixStack matrices = context.matrixStack();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.enableTexture();
//            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

            matrices.push();
//            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0f));
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();

            float k = 100f;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.setShaderTexture(0, EYE);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrices.pop();
        });
    }
}
