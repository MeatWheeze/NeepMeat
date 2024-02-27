package com.neep.meatlib.graphics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

@Environment(value= EnvType.CLIENT)
public interface GraphicsEffect
{
    GraphicsEffectType EMPTY = new GraphicsEffectType();

    void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta);

    default void renderAfter(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta) {}

    void tick();

    boolean isRemoved();

    World getWorld();
}
