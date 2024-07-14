package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChopMeatgunAnimation extends AnimatedAction<MeatgunComponent, ChopMeatgunAnimation> implements MeatgunAnimation
{
    private final RenderAction.Sequence<ChopMeatgunAnimation> down = (parent, counter, tickDelta) ->
    {
        if (counter > 10)
            setSequence(this.up);
    };

    private final RenderAction.Sequence<ChopMeatgunAnimation> up = (parent, counter, tickDelta) ->
    {
        if (counter > 10)
            markFinished();
    };

    @Override
    public boolean canStop()
    {
        return false;
    }
}
