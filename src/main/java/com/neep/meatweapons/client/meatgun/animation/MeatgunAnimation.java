package com.neep.meatweapons.client.meatgun.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface MeatgunAnimation
{
    boolean canStop();

    boolean finished();

    void tick();

    MeatgunAnimation EMPTY = new MeatgunAnimation()
    {
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
        public void tick()
        {

        }
    };
}
