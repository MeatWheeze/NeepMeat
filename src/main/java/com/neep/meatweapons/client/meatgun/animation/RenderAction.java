package com.neep.meatweapons.client.meatgun.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface RenderAction<E>
{
    default void start() {}

    void tick();

    interface Sequence<T>
    {
        void tick(T parent, int counter);

        void applyRender(MatrixStack matrices, int counter, float tickDelta);
    }
}
