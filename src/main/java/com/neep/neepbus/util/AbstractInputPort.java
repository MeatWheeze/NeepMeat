package com.neep.neepbus.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public abstract class AbstractInputPort implements NeepBusPort
{
    private final List<Runnable> invalidate = new ObjectArrayList<>();

    @Override
    public void addInvalidateListener(Runnable invalidate)
    {
        this.invalidate.add(invalidate);
    }

    public void invalidateAddress()
    {
        invalidate.forEach(Runnable::run);
        invalidate.clear();
    }
}
