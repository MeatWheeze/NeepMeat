package com.neep.neepmeat.client;

import com.neep.neepmeat.network.plc.PLCErrorMessageS2C;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.network.plc.PLCSyncProgram;

public class NMClientNetwork
{
    public static void init()
    {
        PLCRobotEnterS2C.Client.registerReceiver();
        PLCSyncProgram.Client.registerReceiver();
        PLCRobotC2S.Client.init();
        PLCErrorMessageS2C.Client.init();
    }
}
