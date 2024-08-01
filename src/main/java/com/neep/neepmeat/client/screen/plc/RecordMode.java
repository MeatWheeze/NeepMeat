package com.neep.neepmeat.client.screen.plc;

import com.neep.meatlib.api.network.ParamCodec;

public enum RecordMode
{
    IMMEDIATE,
    EDIT;

    public static final ParamCodec<RecordMode> PARAM_CODEC = ParamCodec.of(RecordMode.class, (o, b) -> b.writeVarInt(o.ordinal()), b -> RecordMode.values()[b.readVarInt()]);

    public static RecordMode cycle(RecordMode mode)
    {
        if (mode == IMMEDIATE)
            return EDIT;
        else
            return IMMEDIATE;
    }
}
