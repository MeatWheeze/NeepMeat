package com.neep.neepbus.block.entity;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface GaugeBlockEntity
{
    @Nullable Text getName();

    int getValue();

    GaugeBlockEntity EMPTY = new GaugeBlockEntity()
    {
        @Override
        public @Nullable Text getName()
        {
            return null;
        }

        @Override
        public int getValue()
        {
            return 0;
        }
    };
}
