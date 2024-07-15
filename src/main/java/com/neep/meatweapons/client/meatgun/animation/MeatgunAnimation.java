package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface MeatgunAnimation
{
    void start();

    boolean canStop();

    boolean finished();

    void tick(MeatgunComponent component);

    void applyRender(MatrixStack matrices, float tickDelta);

    MeatgunAnimation EMPTY = new MeatgunAnimation()
    {
        @Override
        public void start() { }

        @Override
        public boolean canStop()
        {
            return true;
        }

        @Override
        public boolean finished()
        {
            return false;
        }

        @Override
        public void tick(MeatgunComponent component)
        {

        }

        @Override
        public void applyRender(MatrixStack matrices, float tickDelta)
        {

        }
    };

}
