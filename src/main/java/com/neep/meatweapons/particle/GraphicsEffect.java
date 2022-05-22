package com.neep.meatweapons.particle;

import com.google.common.collect.Maps;
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
import java.util.Map;

public abstract class GraphicsEffect
{
    @Environment(value=EnvType.CLIENT)
//    public static Map<ClientWorld, List<GraphicsEffect>> MAP = Maps.newIdentityHashMap();
    public static List<GraphicsEffect> EFFECTS = new ArrayList<>();

//    @Environment(value=EnvType.CLIENT)
//    public static List<GraphicsEffect> getOrCreate(ClientWorld world)
//    {
//        List<GraphicsEffect> effects;
//        if ((effects = MAP.get(world)) == null)
//        {
//            effects = new ArrayList<>();
//            MAP.put(world, effects);
//        }
//        return effects;
//    }

    @Environment(value=EnvType.CLIENT)
    public static void addEffect(GraphicsEffect effect)
    {
//        getOrCreate(effect.world).add(effect);
        EFFECTS.add(effect);
    }

    protected ClientWorld world;
    protected long time;
    public boolean alive = true;

    public GraphicsEffect(ClientWorld world)
    {
        this.world = world;
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
    public abstract void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers);

    public boolean isDead()
    {
        return !alive;
    }

    static
    {
        ClientTickEvents.END_WORLD_TICK.register(ctx ->
        {
            ClientWorld world = MinecraftClient.getInstance().world;
//            List<GraphicsEffect> effects = getOrCreate(world);
            EFFECTS.removeIf(effect -> effect.isDead() || effect.getWorld() != world);
            EFFECTS.forEach(GraphicsEffect::tick);
        });

        WorldRenderEvents.BEFORE_ENTITIES.register(ctx ->
        {
            MatrixStack matrices = ctx.matrixStack();
            VertexConsumerProvider consumers = ctx.consumers();

            EFFECTS.forEach(((effect) ->
                    effect.render(ctx.camera(), matrices, consumers)));
        });
    }

    @FunctionalInterface
    public interface Factory
    {
        GraphicsEffect create(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, int maxTime);
    }
}
