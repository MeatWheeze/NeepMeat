package com.neep.neepmeat.client.screen.plc;

public enum RecordMode
{
    IMMEDIATE,
    EDIT;

    public static RecordMode cycle(RecordMode mode)
    {
        if (mode == IMMEDIATE)
            return EDIT;
        else
            return IMMEDIATE;
    }
}
