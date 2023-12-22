package com.neep.meatweapons.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphicsEffect
{
    @Environment(value=EnvType.CLIENT)
    public static List<GraphicsEffect> EFFECTS = new ArrayList<>();

    @Environment(value=EnvType.CLIENT)
    public static void addEffect(GraphicsEffect effect)
    {
        EFFECTS.add(effect);
    }

    protected ClientWorld world;
    protected long time;
    public boolean alive = true;
    protected Vec3d start;
    protected Vec3d end;
    protected Vec3d velocity;
    protected int maxTime;
    protected float scale;

    public GraphicsEffect(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        this.world = world;
        this.start = start;
        this.end = end;
        this.velocity = velocity;
        this.maxTime = maxTime;
        this.scale = scale;
    }

    public ClientWorld getWorld()
    {
        return world;
    }

    public void tick()
    {
        if (!this.alive)
            return;

        ++time;
    }

    @Environment(value=EnvType.CLIENT)
    public abstract void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta);

    public boolean isDead()
    {
        return !alive;
    }

    static
    {
        ClientTickEvents.END_WORLD_TICK.register(ctx ->
        {
            ClientWorld world = MinecraftClient.getInstance().world;
            EFFECTS.removeIf(effect -> effect.isDead() || effect.getWorld() != world);
            EFFECTS.forEach(GraphicsEffect::tick);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(ctx ->
        {
            MatrixStack matrices = ctx.matrixStack();
            VertexConsumerProvider consumers = ctx.consumers();

            EFFECTS.forEach(((effect) -> effect.render(ctx.camera(), matrices, consumers, ctx.tickDelta())));
        });
    }

    @FunctionalInterface
    public interface Factory
    {
        GraphicsEffect create(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime);
    }
}
