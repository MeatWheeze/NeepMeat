package com.neep.neepbus.util;

import java.util.function.ObjIntConsumer;

public class SimpleOutputPort
{
    private final NeepBusConfig.Entry entry;
    private final ObjIntConsumer<String> sender;

    public SimpleOutputPort(NeepBusConfig.Entry entry, ObjIntConsumer<String> sender)
    {
        this.entry = entry;
        this.sender = sender;
    }

    public void send(int data)
    {
        sender.accept(entry.getAddress(), data);
    }

    // A stopgap until we decide whether to handle doubles instead of ints.
    public void send(double data)
    {
        send((int) Math.round(data));
    }

    public NeepBusConfig.Entry entry()
    {
        return entry;
    }
}
