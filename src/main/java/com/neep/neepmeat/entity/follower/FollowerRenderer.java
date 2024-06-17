package com.neep.neepmeat.entity.follower;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FollowerRenderer extends EntityRenderer<FollowerEntity>
{
    public static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/entity/follower/main.png");

    private final MinecraftClient client = MinecraftClient.getInstance();

    public FollowerRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void render(FollowerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int light)
    {
//        Vec3d vector = entity.getLerpedPos(tickDelta).subtract(client.player.getLerpedPos(tickDelta)).normalize();
        Vec3d entityPos = entity.getLeashPos(tickDelta);
        Vec3d vector = entityPos.subtract(client.gameRenderer.getCamera().getPos()).normalize();
        float dot = MathHelper.clamp((float) (1 - (vector.dotProduct(client.player.getRotationVec(tickDelta)))), 0, 1);
        dot = dot * dot;

        VertexConsumer consumer = vcp.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        float offset = (float) (entity.getLerpedPos(tickDelta).distanceTo(client.player.getLerpedPos(tickDelta)) / 8.0f);
        int num = 10;
        for (int i = 0; i < num; ++i)
        {
            matrices.push();
            float frac = ((i / (float) num) + offset) % (float) num;
            float x = MathHelper.sin(frac * MathHelper.PI * 2);
            float z = MathHelper.cos(frac * MathHelper.PI * 2);
            float y = MathHelper.sin(frac * MathHelper.PI * 8);
            matrices.translate(x, y, z);
            matrices.multiply(new Quaternionf(client.gameRenderer.getCamera().getRotation()));
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(frac * MathHelper.PI));
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotation((1 - frac) * MathHelper.PI));
            glonkyQuad(consumer, matrices.peek(), 0.5f, 1, 1, 1, dot, light);
            matrices.translate(0, 1, 0);
            glonkyQuad(consumer, matrices.peek(), 1.9f, 1, 1, 1, dot, light);
            matrices.pop();
        }
    }

    @Override
    public Vec3d getPositionOffset(FollowerEntity entity, float tickDelta)
    {
        return super.getPositionOffset(entity, tickDelta);
    }

    @Override
    public Identifier getTexture(FollowerEntity entity)
    {
        return TEXTURE;
    }

    private static void glonkyQuad(VertexConsumer consumer, MatrixStack.Entry matrixEntry, float radius, float r, float g, float b, float a, int light)
    {
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();

        for (int i = 0; i < 3; ++i)
        {
            float ang = i / 4f * MathHelper.PI * 2;
            float u = 0.5f + MathHelper.sin(ang);
            float v = 0.5f + MathHelper.cos(ang);
            float x = radius * MathHelper.sin(i / 3f * MathHelper.PI * 2);
            float z = radius * MathHelper.cos(i / 3f * MathHelper.PI * 2);

            Vector4f vector4f = matrix4f.transform(new Vector4f(x, 0, z, 1));
            consumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a, u, v, OverlayTexture.DEFAULT_UV, light, 0, 1, 0);
        }
        Vector4f vector4f = matrix4f.transform(new Vector4f(1, 0.5f, 0, 1));
        consumer.vertex(vector4f.x, vector4f.y, vector4f.z, r, g, b, a, 0, 0, OverlayTexture.DEFAULT_UV, light, 0, 1, 0);
    }
}
