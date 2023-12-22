package com.neep.neepmeat.network.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.program.PlcProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class PLCSyncProgram
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_sync_program");

    public static void send()
    {

    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void syncProgram(PLCBlockEntity be, PlcProgram program)
        {

        }
    }
}
