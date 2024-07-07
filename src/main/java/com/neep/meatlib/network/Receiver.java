package com.neep.meatlib.network;

public interface Receiver<T>
{
    static <T> Receiver<T> empty()
    {
        return (Receiver<T>) DEFAULT;
    }

    void close();

    Receiver<Object> DEFAULT = () -> {};
}
