package com.neep.meatweapons.client;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.nio.FloatBuffer;

@Environment(value= EnvType.CLIENT)
public class BeamRenderer
{
    public static void init()
    {
        WorldRenderEvents.BEFORE_ENTITIES.register((ctx) ->
        {
//            MatrixStack matrices = ctx.matrixStack();
//
//            float thickness = 0.5f;
//
//            matrices.push();
//            Vec3d pos = ctx.camera().getPos();
//
//            Vec3d offset = p0.subtract(pos);
//            matrices.translate(offset.x, offset.y, offset.z);
//
//            VertexConsumer vertexConsumer = ctx.consumers().getBuffer(LAYER_TEST);
//
//            renderBeam(matrices, vertexConsumer, p0, p1, 255, 255, 255, thickness);
//            matrices.pop();

        });
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d cameraPos, Vec3d p0, Vec3d p1, int r, int g, int b, int a, float t)
    {
        matrices.push();
        Vec3d beam = p1.subtract(p0);

        Vec3d offset = p0.subtract(cameraPos);
        matrices.translate(offset.x, offset.y, offset.z);

        // Transform from world to local space
        Matrix4f transform = new Matrix4f();
        transform.loadIdentity();
        float time = MinecraftClient.getInstance().world.getTime() + MinecraftClient.getInstance().getTickDelta();
        beam.normalize();
        transform.multiply(rotationMatrix((float) Math.atan2(beam.getX(), beam.getZ()), 0, (float) Math.asin(-beam.getY() / beam.length())));

        matrices.method_34425(transform);

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f model = entry.getModel();
        Matrix3f normal = entry.getNormal();
        int light = 255;

        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(0, -t / 2, 0), 0, 1); // p0
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(0, -t / 2, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(0, t / 2, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(0, t / 2, 0), 0, 0); // p0

        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(-t / 2, 0, 0), 0, 1); // p0
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(-t / 2, 0, beam.length()), 1, 1); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(t / 2, 0, beam.length()), 1, 0); // p1
        vertex(vertexConsumer, model, normal, r, g, b, a, light, new Vec3d(t / 2, 0, 0), 0, 0); // p0

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
