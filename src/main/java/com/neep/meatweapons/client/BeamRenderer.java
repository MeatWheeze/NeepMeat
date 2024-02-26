package com.neep.meatweapons.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


@Environment(value= EnvType.CLIENT)
public class BeamRenderer
{
    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d cameraPos, Vec3d p0, Vec3d p1, int r, int g, int b, int a, float t, int l)
    {
        renderBeam(matrices, vertexConsumer, cameraPos, p0, p1, 0, r, g, b, a, t, l);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d cameraPos, Vec3d p0, Vec3d p1, float rollDegrees, int r, int g, int b, int a, float t, int l)
    {
        matrices.push();
        Vec3d beam = p1.subtract(p0);

        Vec3d offset = p0.subtract(cameraPos);
        matrices.translate(offset.x, offset.y, offset.z);

        double pitch = Math.atan2(beam.getX(), beam.getZ());
        double yaw = Math.asin(-beam.getY() / beam.length());

        matrices.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float) pitch));
        matrices.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float) yaw));
        Matrix3f normal = matrices.peek().getNormalMatrix().copy();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rollDegrees));

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f model = entry.getPositionMatrix();

        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(0, -t / 2, 0), 0, 1); // p0
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(0, -t / 2, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(0, t / 2, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(0, t / 2, 0), 0, 0); // p0

        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(-t / 2, 0, 0), 0, 1); // p0
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(-t / 2, 0, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(t / 2, 0, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, l, new Vec3d(t / 2, 0, 0), 0, 0); // p0

        matrices.pop();
    }

    public static void renderTube(Vec3d camPos, Vec3d p0, Vec3d p1, float tickDelta, int age, MatrixStack matrices, VertexConsumer consumer, int light)
    {
        matrices.push();
        Vec3d beam = p1.subtract(p0);
        float g = (float) beam.length();

        Vec3d offset = p0.subtract(camPos);
        matrices.translate(offset.x, offset.y, offset.z);

        double pitch = Math.atan2(beam.getX(), beam.getZ());
        double yaw = Math.asin(-beam.getY() / beam.length());

        matrices.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float) pitch));
        matrices.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float) yaw));

        float h = 0.0F - (age + tickDelta) * 0.04F;
        float i = g / 32.0F - (age + tickDelta) * 0.04F;
        float k = 0.0F;
        float m = 0.0F;
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        float radius = 0.75f;

        float l = radius;
        for (int seg = 1; seg <= 8; ++seg)
        {
            float o = MathHelper.sin(seg * 6.2831855F / 8.0F) * radius;
            float p = MathHelper.cos(seg * 6.2831855F / 8.0F) * radius;
            float q = seg / 8.0F;
            consumer.vertex(matrix4f, k, l, 0.0F).color(255, 255, 255, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
            consumer.vertex(matrix4f, k, l, g).color(255, 255, 255, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
            consumer.vertex(matrix4f, o, p, g).color(255, 255, 255, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
            consumer.vertex(matrix4f, o, p, 0.0F).color(255, 255, 255, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
//            vertex(consumer, matrix4f, matrix3f, 255, 255, 255, 255, light, new Vec3d(k*.2, l*.2, 0), 0, 1);
//            vertex(consumer, matrix4f, matrix3f, 255, 255, 255, 255, light, new Vec3d(k, l, g), 0, 1);
//            vertex(consumer, matrix4f, matrix3f, 255, 255, 255, 255, light, new Vec3d(o, p, g), 0, 1);
//            vertex(consumer, matrix4f, matrix3f, 255, 255, 255, 255, light, new Vec3d(o*.2, p*.2, 0), 0, 1);
            k = o;
            l = p;
            m = q;
        }

        matrices.pop();
    }

    public static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int r, int g, int b, int a, int light, Vec3d pos, int u, int v)
    {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0f, 1.0f, 0.0f)
                .next();
    }
}
