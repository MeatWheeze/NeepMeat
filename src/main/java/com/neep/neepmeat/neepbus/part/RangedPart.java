package com.neep.neepmeat.neepbus.part;

public interface RangedPart
{
    int getValue();
    void setValue(int value);

    int getMinValue();
    void setMinValue(int minValue);

    int getMaxValue();
    void setMaxValue(int maxValue);
}
