package com.neep.neepbus.util;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;

public class DirectReadPort implements ReadPort
{
    private final ConfigEntry entry;
    private final ObjIntConsumer<String> sender;
    private final IntSupplier read;

    public DirectReadPort(ConfigEntry entry, IntSupplier read, ObjIntConsumer<String> sender)
    {
        this.entry = entry;
        this.read = read;
        this.sender = sender;
    }

//    public DirectReadPort(ConfigEntry entry, ObjIntConsumer<String> sender, DoubleSupplier read)
//    {
//        this.entry = entry;
//        this.sender = sender;
//
//        // A stopgap until we decide whether to handle doubles instead of ints.
//        this.read = () -> (int) Math.round(read.getAsDouble());
//    }

    public void send()
    {
        sender.accept(entry.getAddress(), read.getAsInt());
    }

//    public void send(double data)
//    {
//        send((int) Math.round(data));
//    }

    @Override
    public int read()
    {
        return read.getAsInt();
    }

    public ConfigEntry entry()
    {
        return entry;
    }
}
