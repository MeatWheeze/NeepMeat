package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface RenderAction<E>
{
    default void start() {}

    void tick(MeatgunComponent component);

    interface Sequence<T>
    {
        void tick(T parent, MeatgunComponent component, int counter);

        void applyRender(MatrixStack matrices, int counter, float tickDelta);
    }
}
