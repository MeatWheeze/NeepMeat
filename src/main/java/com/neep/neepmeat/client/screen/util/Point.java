package com.neep.neepmeat.client.screen.util;

public interface Point
{
    int x();

    int y();

    interface Mutable extends Point
    {
        void setPos(int x, int y);
    }
}
