package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MeatgunAnimationManager
{
    private final MeatgunComponent component;
    private MeatgunAnimation activeAnimation = MeatgunAnimation.EMPTY;

    public MeatgunAnimationManager(MeatgunComponent component)
    {
        this.component = component;
    }

    public void queue(String animation)
    {
        // ???
    }

    public void queue(MeatgunAnimation animation)
    {
        if (activeAnimation.canStop())
        {
            activeAnimation = animation;
        }
    }

    public void tick()
    {
        if (activeAnimation.finished())
            activeAnimation = MeatgunAnimation.EMPTY;

        activeAnimation.tick();
    }
}
