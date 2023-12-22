package com.neep.neepmeat.client.screen.plc;

public enum RecordMode
{
    IMMEDIATE,
    RECORD;

    public static RecordMode cycle(RecordMode mode)
    {
        if (mode == IMMEDIATE)
            return RECORD;
        else
            return IMMEDIATE;
    }
}
