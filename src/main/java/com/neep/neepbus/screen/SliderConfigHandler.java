package com.neep.neepbus.screen;

import com.neep.neepbus.part.Slider;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class SliderConfigHandler extends RangeConfigHandler
{
    private final Slider slider;

    public SliderConfigHandler(PlayerEntity player, Slider ranged)
    {
        super(player, ranged);
        this.slider = ranged;
    }

    @Override
    protected void receiveParams(List<Integer> ints)
    {
        super.receiveParams(ints);

//        slider.setValue(ints.get(0));
        slider.setInterval(ints.get(3));
    }

    @Override
    public void sendUpdates()
    {
        if (updateToClient)
        {
            // Send the updated (and possibly sanitised) values back to the client.
            updateParamsS2C.emitter().update(List.of(slider.getValue(), slider.getMinValue(), slider.getMaxValue(), slider.getInterval()));
            updateToClient = false;
        }
    }
}
