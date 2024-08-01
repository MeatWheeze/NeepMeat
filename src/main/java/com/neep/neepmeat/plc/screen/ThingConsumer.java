package com.neep.neepmeat.plc.screen;

import java.util.function.Consumer;

public interface ThingConsumer<T> extends Consumer<T>
{
    static <T> Class<Consumer<T>> as()
    {
        return (Class<Consumer<T>>) (Object) Consumer.class;
    }
}
