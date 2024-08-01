package com.neep.neepmeat.network.plc;

import com.neep.meatlib.api.network.ParamCodec;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public enum PLCSyncAction
{
    PROGRAM,
    RUN,
    PAUSE,
    STOP,
    COMPILE, COMPILE_RUN;

    public static void sendCompileStatus(ServerPlayerEntity controller, String message, boolean b, int line)
    {
        if (controller.currentScreenHandler instanceof PLCScreenHandler handler)
        {
            handler.compileMessageS2C.emitter().apply(message, b, line);
        }
    }

    public static final ParamCodec<PLCSyncAction> PARAM_CODEC = ParamCodec.of(PLCSyncAction.class, (o, b) -> b.writeVarInt(o.ordinal()), b -> PLCSyncAction.values()[b.readVarInt()]);
}
