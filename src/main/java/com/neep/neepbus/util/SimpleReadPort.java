package com.neep.neepbus.util;

import com.neep.meatlib.util.NbtSerialisable;
import net.minecraft.nbt.NbtCompound;

import java.util.function.ObjIntConsumer;

/**
 * Remembers the last sent value and allows it to be read from elsewhere.
 * Don't forget to save NBT,
 */
public class SimpleReadPort implements ReadPort, NbtSerialisable
{
    private final ConfigEntry entry;
    private final ObjIntConsumer<String> sender;

    private int lastValue;

    public SimpleReadPort(ConfigEntry entry, ObjIntConsumer<String> sender)
    {
        this.entry = entry;
        this.sender = sender;
    }

    public void send(int data)
    {
        lastValue = data;
        sender.accept(entry.getAddress(), data);
    }

    public void send(double data)
    {
        send((int) Math.round(data));
    }

    @Override
    public int read()
    {
        return lastValue;
    }

    public ConfigEntry entry()
    {
        return entry;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("last_value", lastValue);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.lastValue = nbt.getInt("last_value");
    }
}
