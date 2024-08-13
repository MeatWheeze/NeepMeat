package com.neep.neepmeat.neepbus.screen;

import com.neep.neepmeat.neepbus.part.Slider;
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
}
