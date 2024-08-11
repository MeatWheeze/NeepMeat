package com.neep.neepmeat.neepbus;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public abstract class SimpleInputPort implements NeepBusPort
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
