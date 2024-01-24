package com.neep.meatweapons.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
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

        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float) pitch));
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) yaw));
        Matrix3f normal = matrices.peek().getNormalMatrix().copy();
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rollDegrees));

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

    public static Vec3f pitchYaw(Vec3d vec)
    {
        vec.normalize();
        float yaw = (float) Math.atan2(vec.z, vec.x);
        float pitch = (float) - Math.asin(vec.y);

        return new Vec3f(pitch, yaw, 0);
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
