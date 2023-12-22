package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.nio.FloatBuffer;

public class BeamRenderer
{
    public static final Identifier TEX_TEST = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/beam.png");
    public static final RenderLayer LAYER_TEST = RenderLayer.getEntityTranslucent(TEX_TEST);
    public static Vec3d p0 = new Vec3d(10, 10, 10);
    public static Vec3d p1 = new Vec3d(20, 20, 20);

    public static void init()
    {
        WorldRenderEvents.BEFORE_ENTITIES.register((ctx) ->
        {
            MatrixStack matrices = ctx.matrixStack();

            float thickness = 0.5f;

            matrices.push();
            Vec3d pos = ctx.camera().getPos();

//            Vec3d p1 = new Vec3d(10, 10, 10);
//            Vec3d p2 = new Vec3d(20, 7, 2);

            Vec3d offset = p0.subtract(pos);
            matrices.translate(offset.x, offset.y, offset.z);

            VertexConsumer vertexConsumer = ctx.consumers().getBuffer(LAYER_TEST);

            renderBeam(matrices, vertexConsumer, p0, p1, 255, 255, 255, thickness);
            matrices.pop();

        });
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d p0, Vec3d p1, int r, int g, int b, float t)
    {
        matrices.push();
        Vec3d beam = p1.subtract(p0);

        // Create transform for world to vector space
        Matrix4f transform = new Matrix4f();
        transform.loadIdentity();
        float time = MinecraftClient.getInstance().world.getTime() + MinecraftClient.getInstance().getTickDelta();
        beam.normalize();
        transform.multiply(rotationMatrix((float) Math.atan2(beam.getX(), beam.getZ()), 0, (float) Math.asin(-beam.getY() / beam.length())));
//        transform.multiply(rotationMatrix(time /40 , 0, (float) Math.asin(-beam.getY() / beam.length())));

        matrices.method_34425(transform);

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        int light = 255;

        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(0, -t / 2, 0), 0, 1); // p0
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(0, -t / 2, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(0, t / 2, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(0, t / 2, 0), 0, 0); // p0

        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(-t / 2, 0, 0), 0, 1); // p0
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(-t / 2, 0, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(t / 2, 0, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, matrix4f, matrix3f, light, new Vec3d(t / 2, 0, 0), 0, 0); // p0

        matrices.pop();
    }

    public static Matrix4f rotationMatrix(float pitch, float yaw, float roll)
    {
        // a, b and g represent yaw, roll and pitch
        float sa = (float) Math.sin(yaw);
        float sb = (float) Math.sin(pitch);
        float sg = (float) Math.sin(roll);
        float ca = (float) Math.cos(yaw);
        float cb = (float) Math.cos(pitch);
        float cg = (float) Math.cos(roll);

        // 3D rotation matrix
        float[] floats = new float[]{
                ca * cb,    ca * sb * sg - sa * cg,     ca * sb * cg + sa * sg,     0f,
                sa * cb,    sa * sb * sg + ca * cg,     sa * sb * cg - ca * sg,     0f,
                -sb,        ca * sg,                    cb * cg,                    0f,
                0f,         0f,                         0f,                         1f
        };

//        float[] floats = new float[]{
//                ca, -sa, 0, 0,
//                sa, ca, 0, 0,
//                0, 0, 1, 0,
//                0, 0, 0, 1
//        };

        // Some unnecessary jank because Matrix4f is final and there are no useful constructors.
        FloatBuffer buffer = FloatBuffer.allocate(16);
        buffer.put(floats);
        Matrix4f matrix = new Matrix4f();
        matrix.readRowMajor(buffer);
        return matrix;
    }

    public static Vec3f pitchYaw(Vec3d vec)
    {
        vec.normalize();
        float yaw = (float) Math.atan2(vec.z, vec.x);
        float pitch = (float) - Math.asin(vec.y);

        return new Vec3f(pitch, yaw, 0);
    }

    public static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, Vec3d pos, int u, int v)
    {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0f, 1.0f, 0.0f)
                .next();
    }

    private static float percentage(int value, int max)
    {
        return (float)value / (float)max;
    }

    private static void fishingRodTest(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry normal, float f, float g) {
        float h = x * f;
        float i = y * (f * f + f) * 0.5f + 0.25f;
        float j = z * f;
        float k = x * g - h;
        float l = y * (g * g + g) * 0.5f + 0.25f - i;
        float m = z * g - j;
        float n = MathHelper.sqrt(k * k + l * l + m * m);
        buffer.vertex(normal.getModel(), h, i, j).color(0, 0, 0, 255).normal(normal.getNormal(), k / n, l / n, m / n).next();
    }
}
