package com.neep.neepbus.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

// Not used
public interface GeneralPort extends WritePort, ReadPort
{
    static GeneralPort of(IntConsumer write, IntSupplier read)
    {
        return new GeneralPort()
        {
            @Override
            public void write(int data)
            {
                write.accept(data);
            }

            @Override
            public int read()
            {
                return read.getAsInt();
            }
        };
    }
}
