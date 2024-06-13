package com.neep.neepmeat.client;

import com.neep.meatlib.util.ClientComponents;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpClient;
import com.neep.neepmeat.machine.live_machine.LivingMachines;

public class NMClientComponents
{
    public static void init()
    {
        ClientComponents.register(LivingMachines.CHARNEL_PUMP_BE, CharnelPumpClient::new);
    }
}
