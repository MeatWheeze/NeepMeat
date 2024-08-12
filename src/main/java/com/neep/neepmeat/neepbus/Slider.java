package com.neep.neepmeat.neepbus;

public interface Slider
{
    int getValue();
    void setValue(int value);

    int getMinValue();
    void setMinValue(int minValue);

    int getMaxValue();
    void setMaxValue(int maxValue);

    int getDivisions();
    void setDivisions(int divisions);

    default void largeIncrement(double amount, boolean large)
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
        public int getDivisions()
        {
            return 0;
        }

        @Override
        public void setDivisions(int divisions)
        {

        }
    };
}
