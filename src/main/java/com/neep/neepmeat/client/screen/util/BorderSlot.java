package com.neep.neepmeat.client.screen.util;

import java.util.function.Supplier;

public class BorderSlot extends Border
{
    public BorderSlot(int x, int y, Supplier<Integer> col)
    {
        super(x, y, 20, 20, 0, col);
    }
}
