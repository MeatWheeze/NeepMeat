package com.neep.neepmeat.neepbus;

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

    public NeepBusConfig.Entry entry()
    {
        return entry;
    }
}
