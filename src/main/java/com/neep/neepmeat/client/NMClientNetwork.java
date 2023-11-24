package com.neep.neepmeat.client;

import com.neep.neepmeat.network.PLCRobotEnterS2C;

public class NMClientNetwork
{
    public static void init()
    {
        PLCRobotEnterS2C.Client.registerReceiver();
    }
}
