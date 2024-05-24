package com.neep.neepmeat.client.screen.button;

import com.neep.neepmeat.client.screen.util.Rectangle;

public abstract class PersistentWidget
{
    protected Rectangle bounds = new Rectangle.Immutable(0, 0, 0, 0);

    public void init(Rectangle bounds)
    {
        this.bounds = bounds;
    }
}
