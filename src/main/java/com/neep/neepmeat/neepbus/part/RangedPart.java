package com.neep.neepmeat.neepbus.part;

public interface RangedPart
{
    int getValue();
    void setValue(int value);

    int getMinValue();
    void setMinValue(int minValue);

    int getMaxValue();
    void setMaxValue(int maxValue);

    class Empty implements RangedPart
    {
        @Override
        public int getValue()
        {
            return 0;
        }

        @Override
        public void setValue(int value)
        {

        }

        @Override
        public int getMinValue()
        {
            return 0;
        }

        @Override
        public void setMinValue(int minValue)
        {

        }

        @Override
        public int getMaxValue()
        {
            return 0;
        }

        @Override
        public void setMaxValue(int maxValue)
        {

        }
    }

    RangedPart EMPTY = new Empty();
}
