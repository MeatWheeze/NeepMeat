package com.neep.neepbus.part;

public interface Slider extends Ranged
{
    int getInterval();
    void setInterval(int interval);

    default void increment(double amount, boolean large)
    {

    }

    Slider EMPTY = new Slider()
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

        @Override
        public int getInterval()
        {
            return 0;
        }

        @Override
        public void setInterval(int interval)
        {

        }
    };
}
