package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.client.meatgun.animation.ChopMeatgunAnimation;
import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MeatgunAnimationManager
{

    private final MeatgunAnimation idle;
    private MeatgunAnimation activeAnimation = MeatgunAnimation.EMPTY;

    private final Map<String, MeatgunAnimation> animations = new HashMap<>();

    public MeatgunAnimationManager(MeatgunAnimation idle)
    {
        this.idle = idle;
        this.activeAnimation = idle;
        this.activeAnimation.start();
    }

    public void queue(String name)
    {
        @Nullable MeatgunAnimation animation = animations.get(name);
        if (animation != null)
            queue(animation);
    }

    public void queue(MeatgunAnimation animation)
    {
        if (activeAnimation.canStop())
        {
            activeAnimation = animation;
            activeAnimation.start();
        }
    }

    public void tick()
    {
        if (activeAnimation.finished())
        {
            activeAnimation = idle;
            activeAnimation.start();
        }

        activeAnimation.tick();
    }

    public MeatgunAnimation getActive()
    {
        return activeAnimation;
    }

    public MeatgunAnimationManager add(String name, MeatgunAnimation animation)
    {
        animations.put(name, animation);
        return this;
    }
}
