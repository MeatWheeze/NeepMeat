package com.neep.neepmeat.init;

import com.neep.neepmeat.network.PLCRobotC2S;
import com.neep.neepmeat.network.PLCRobotEnterS2C;

public class NMCommonNetwork
{
    public static void init()
    {
        PLCRobotC2S.init();
        PLCRobotEnterS2C.init();
    }
}
