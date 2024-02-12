package com.neep.neepmeat.init;

import com.neep.neepmeat.network.ScreenPropertyC2SPacket;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.network.plc.PLCSyncThings;

public class NMCommonNetwork
{
    public static void init()
    {
        PLCRobotC2S.init();
        PLCRobotEnterS2C.init();
        PLCSyncThings.init();
        ScreenPropertyC2SPacket.init();
    }
}
