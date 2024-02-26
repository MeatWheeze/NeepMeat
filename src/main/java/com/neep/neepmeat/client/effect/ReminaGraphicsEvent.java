package com.neep.neepmeat.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.meatlib.graphics.GraphicsEffect;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.UUID;

public class ReminaGraphicsEvent implements GraphicsEffect
{
    protected final UUID uuid;
    protected final World world;

    private static final Identifier EYE = new Identifier(NeepMeat.NAMESPACE, "textures/world/object.png");

    public ReminaGraphicsEvent(World world, UUID uuid, PacketByteBuf buf)
    {
        this.uuid = uuid;
        this.world = world;
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
    }

    @Override
    public void renderAfter(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

//        RenderSystem.enableTexture();
//            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        matrices.push();
//            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0f));
        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();

        float k = 100f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.setShaderTexture(0, EYE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();
        var built = bufferBuilder.end();
        BufferRenderer.draw(built);
        matrices.pop();
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean isRemoved()
    {
        return false;
    }

    @Override
    public World getWorld()
    {
        return world;
    }
}
