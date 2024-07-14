package com.neep.meatweapons.client.meatgun.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderAction<E>
{
    default void start() {}

    void tick();

    @FunctionalInterface
    interface Sequence<T>
    {
        void tick(T parent, int counter, float tickDelta);
    }
}
